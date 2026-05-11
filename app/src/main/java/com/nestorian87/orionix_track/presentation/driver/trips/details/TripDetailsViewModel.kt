package com.nestorian87.orionix_track.presentation.driver.trips.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nestorian87.orionix_track.domain.error.AppError
import com.nestorian87.orionix_track.domain.model.Trip
import com.nestorian87.orionix_track.domain.model.TripStatus
import com.nestorian87.orionix_track.domain.repository.TripRepository
import com.nestorian87.orionix_track.domain.repository.TripRealtimeRepository
import com.nestorian87.orionix_track.domain.realtime.TripRealtimeEvent
import com.nestorian87.orionix_track.domain.result.AppResult
import com.nestorian87.orionix_track.presentation.common.navigation.AppDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class TripDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val tripRepository: TripRepository,
    private val tripRealtimeRepository: TripRealtimeRepository
) : ViewModel() {
    private val tripId: Long = savedStateHandle.get<String>(AppDestination.TRIP_ID_ARG)?.toLongOrNull() ?: -1L

    private val _uiState = MutableStateFlow(TripDetailsUiState())
    val uiState: StateFlow<TripDetailsUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<TripDetailsEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<TripDetailsEvent> = _events.asSharedFlow()

    private var realtimeJob: Job? = null
    private var observedTripId: Long? = null

    init {
        refresh()
    }

    fun refresh() {
        if (tripId <= 0L) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isUnavailable = true
                )
            }
            _events.tryEmit(TripDetailsEvent.NavigateBackToList)
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = tripRepository.getTripDetails(tripId)) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(
                            trip = result.data,
                            isLoading = false,
                            isUnavailable = false,
                            error = null
                        )
                    }
                    observeRealtimeIfNeeded(result.data)
                }
                is AppResult.Failure -> onUnavailableIfNeeded(result.error) {
                    _uiState.update {
                        it.copy(
                            trip = null,
                            isLoading = false,
                            error = result.error
                        )
                    }
                }
            }
        }
    }

    fun startTrip() = updateTripStatus(TripStatus.PLANNED)

    fun endTrip() = updateTripStatus(TripStatus.IN_PROGRESS)

    private fun updateTripStatus(expectedCurrentStatus: TripStatus) {
        val trip = _uiState.value.trip ?: return
        if (_uiState.value.isActionLoading || trip.status != expectedCurrentStatus) return

        viewModelScope.launch {
            _uiState.update { it.copy(isActionLoading = true, error = null) }
            val actionResult = if (expectedCurrentStatus == TripStatus.PLANNED) {
                tripRepository.startTrip(trip.id)
            } else {
                tripRepository.endTrip(trip.id)
            }
            when (actionResult) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(
                            trip = actionResult.data,
                            isActionLoading = false,
                            error = null
                        )
                    }
                    observeRealtimeIfNeeded(actionResult.data)
                }
                is AppResult.Failure -> onUnavailableIfNeeded(actionResult.error) {
                    _uiState.update {
                        it.copy(
                            isActionLoading = false,
                            error = actionResult.error
                        )
                    }
                    if (shouldRefreshOnStatusConflict(actionResult.error)) {
                        refresh()
                    }
                }
            }
        }
    }

    private fun observeRealtimeIfNeeded(trip: Trip) {
        if (trip.status != TripStatus.IN_PROGRESS) {
            stopRealtime()
            return
        }
        if (observedTripId == trip.id && realtimeJob?.isActive == true) return

        stopRealtime()
        observedTripId = trip.id
        realtimeJob = viewModelScope.launch {
            tripRealtimeRepository.observeTrip(trip.id).collect { event ->
                when (event) {
                    is TripRealtimeEvent.TelemetryUpdated -> {
                        _uiState.update { state ->
                            state.copy(
                                trip = state.trip?.takeIf { it.id == event.tripId }
                                    ?.copy(currentTelemetry = event.telemetry) ?: state.trip
                            )
                        }
                    }
                    is TripRealtimeEvent.StatusChanged -> {
                        _uiState.update { state ->
                            state.copy(
                                trip = state.trip?.takeIf { it.id == event.tripId }
                                    ?.copy(status = event.status) ?: state.trip
                            )
                        }
                        if (event.status.isTerminal) {
                            stopRealtime()
                            refresh()
                        }
                    }
                    TripRealtimeEvent.Reconnected -> refresh()
                    TripRealtimeEvent.Connected,
                    TripRealtimeEvent.Disconnected,
                    is TripRealtimeEvent.Error -> Unit
                }
            }
        }
    }

    private fun stopRealtime() {
        realtimeJob?.cancel()
        realtimeJob = null
        observedTripId = null
    }

    private fun onUnavailableIfNeeded(
        error: AppError,
        onContinue: () -> Unit
    ) {
        val isUnavailable = error is AppError.Server &&
            error.code == 404 &&
            error.message.equals("Trip not found or not assigned to you", ignoreCase = true)
        if (isUnavailable) {
            _uiState.update {
                it.copy(
                    isUnavailable = true,
                    isLoading = false,
                    isActionLoading = false,
                    error = error
                )
            }
            _events.tryEmit(TripDetailsEvent.NavigateBackToList)
        } else {
            onContinue()
        }
    }

    private fun shouldRefreshOnStatusConflict(error: AppError): Boolean {
        if (error !is AppError.Server || error.code !in setOf(400, 403)) return false
        val message = error.message.orEmpty().lowercase()
        return message.contains("can only start planned trips") ||
            message.contains("can only end trips that are in progress")
    }

    private val TripStatus.isTerminal: Boolean
        get() = this == TripStatus.COMPLETED || this == TripStatus.CANCELLED
}
