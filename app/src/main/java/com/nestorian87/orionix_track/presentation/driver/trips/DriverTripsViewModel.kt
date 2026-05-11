package com.nestorian87.orionix_track.presentation.driver.trips

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nestorian87.orionix_track.domain.model.Trip
import com.nestorian87.orionix_track.domain.model.TripStatus
import com.nestorian87.orionix_track.domain.repository.TripRepository
import com.nestorian87.orionix_track.domain.repository.TripRealtimeRepository
import com.nestorian87.orionix_track.domain.realtime.TripRealtimeEvent
import com.nestorian87.orionix_track.domain.result.AppResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class DriverTripsViewModel @Inject constructor(
    private val tripRepository: TripRepository,
    private val tripRealtimeRepository: TripRealtimeRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(DriverTripsUiState())
    val uiState: StateFlow<DriverTripsUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null
    private var historySearchJob: Job? = null
    private var realtimeJob: Job? = null
    private var observedTripIds: Set<Long> = emptySet()

    init {
        loadActiveTrip()
        loadAssignedTrips(reset = true)
        loadHistoryTrips(reset = true)
    }

    fun onSearchQueryChanged(value: String) {
        _uiState.update { it.copy(searchQuery = value) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(450)
            loadAssignedTrips(reset = true)
        }
    }

    fun onHistorySearchQueryChanged(value: String) {
        _uiState.update { it.copy(historySearchQuery = value) }
        historySearchJob?.cancel()
        historySearchJob = viewModelScope.launch {
            delay(450)
            loadHistoryTrips(reset = true)
        }
    }

    fun loadMoreAssignedTrips() {
        val currentState = _uiState.value
        if (currentState.assignedTripsAppending ||
            currentState.assignedTripsLoading ||
            !currentState.hasMoreAssignedTrips
        ) return
        loadAssignedTrips(reset = false)
    }

    fun loadMoreHistoryTrips() {
        val currentState = _uiState.value
        if (currentState.historyTripsAppending ||
            currentState.historyTripsLoading ||
            !currentState.hasMoreHistoryTrips
        ) return
        loadHistoryTrips(reset = false)
    }

    fun refreshAssignedTrips() {
        loadActiveTrip()
        loadAssignedTrips(reset = true)
    }

    fun refreshHistoryTrips() {
        loadHistoryTrips(reset = true)
    }

    fun refreshCurrentTrips() {
        loadActiveTrip()
        loadAssignedTrips(reset = true)
    }

    private fun loadActiveTrip() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    activeTripLoading = true,
                    activeTripError = null
                )
            }

            when (val result = tripRepository.getActiveTrip()) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(
                            activeTrip = result.data,
                            activeTripLoading = false,
                            activeTripError = null
                        )
                    }
                    observeCurrentTripsRealtime()
                }
                is AppResult.Failure -> {
                    _uiState.update {
                        it.copy(
                            activeTripLoading = false,
                            activeTripError = result.error
                        )
                    }
                }
            }
        }
    }

    private fun loadAssignedTrips(reset: Boolean) {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (reset) {
                stopCurrentTripsRealtime()
                _uiState.update {
                    it.copy(
                        assignedTrips = emptyList(),
                        assignedTripsLoading = true,
                        assignedTripsAppending = false,
                        assignedTripsError = null,
                        hasMoreAssignedTrips = true
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        assignedTripsAppending = true,
                        assignedTripsError = null
                    )
                }
            }

            val offset = if (reset) 0 else currentState.assignedTrips.size
            when (
                val result = tripRepository.getAssignedTrips(
                    limit = PAGE_SIZE,
                    offset = offset,
                    search = _uiState.value.searchQuery.trim()
                )
            ) {
                is AppResult.Success -> {
                    _uiState.update { state ->
                        val mergedTrips = if (reset) result.data else state.assignedTrips + result.data
                        state.copy(
                            assignedTrips = mergedTrips.distinctBy { it.id },
                            assignedTripsLoading = false,
                            assignedTripsAppending = false,
                            assignedTripsError = null,
                            hasMoreAssignedTrips = result.data.size == PAGE_SIZE
                        )
                    }
                    observeCurrentTripsRealtime()
                }
                is AppResult.Failure -> {
                    _uiState.update {
                        it.copy(
                            assignedTripsLoading = false,
                            assignedTripsAppending = false,
                            assignedTripsError = result.error
                        )
                    }
                    observeCurrentTripsRealtime()
                }
            }
        }
    }

    private fun loadHistoryTrips(reset: Boolean) {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (reset) {
                _uiState.update {
                    it.copy(
                        historyTrips = emptyList(),
                        historyTripsLoading = true,
                        historyTripsAppending = false,
                        historyTripsError = null,
                        hasMoreHistoryTrips = true
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        historyTripsAppending = true,
                        historyTripsError = null
                    )
                }
            }

            val offset = if (reset) 0 else currentState.historyTrips.size
            when (
                val result = tripRepository.getTripHistory(
                    limit = PAGE_SIZE,
                    offset = offset,
                    search = _uiState.value.historySearchQuery.trim()
                )
            ) {
                is AppResult.Success -> {
                    _uiState.update { state ->
                        val mergedTrips = if (reset) result.data else state.historyTrips + result.data
                        state.copy(
                            historyTrips = mergedTrips.distinctBy { it.id },
                            historyTripsLoading = false,
                            historyTripsAppending = false,
                            historyTripsError = null,
                            hasMoreHistoryTrips = result.data.size == PAGE_SIZE
                        )
                    }
                }
                is AppResult.Failure -> {
                    _uiState.update {
                        it.copy(
                            historyTripsLoading = false,
                            historyTripsAppending = false,
                            historyTripsError = result.error
                        )
                    }
                }
            }
        }
    }

    private fun observeCurrentTripsRealtime() {
        val state = _uiState.value
        val tripIds = (state.assignedTrips.map { it.id } + listOfNotNull(state.activeTrip?.id)).toSet()

        if (tripIds == observedTripIds) return
        observedTripIds = tripIds
        realtimeJob?.cancel()

        if (tripIds.isEmpty()) return

        realtimeJob = viewModelScope.launch {
            tripRealtimeRepository.observeTrips(tripIds).collect { event ->
                when (event) {
                    is TripRealtimeEvent.TelemetryUpdated -> updateAssignedTrip(event.tripId) { trip ->
                        trip.copy(currentTelemetry = event.telemetry)
                    }
                    is TripRealtimeEvent.StatusChanged -> {
                        if (event.status.isTerminal) {
                            _uiState.update { state ->
                                state.copy(
                                    activeTrip = state.activeTrip?.takeUnless { it.id == event.tripId },
                                    assignedTrips = state.assignedTrips.filterNot { it.id == event.tripId }
                                )
                            }
                            observeCurrentTripsRealtime()
                            loadHistoryTrips(reset = true)
                        } else {
                            updateAssignedTrip(event.tripId) { trip ->
                                trip.copy(status = event.status)
                            }
                        }
                    }
                    TripRealtimeEvent.Reconnected -> refreshCurrentTrips()
                    TripRealtimeEvent.Connected,
                    TripRealtimeEvent.Disconnected,
                    is TripRealtimeEvent.Error -> Unit
                }
            }
        }
    }

    private fun stopCurrentTripsRealtime() {
        realtimeJob?.cancel()
        realtimeJob = null
        observedTripIds = emptySet()
    }

    private fun updateAssignedTrip(
        tripId: Long,
        transform: (Trip) -> Trip
    ) {
        _uiState.update { state ->
            state.copy(
                activeTrip = state.activeTrip?.let { trip ->
                    if (trip.id == tripId) transform(trip) else trip
                },
                assignedTrips = state.assignedTrips.map { trip ->
                    if (trip.id == tripId) transform(trip) else trip
                }
            )
        }
    }

    private val TripStatus.isTerminal: Boolean
        get() = this == TripStatus.COMPLETED || this == TripStatus.CANCELLED

    companion object {
        private const val PAGE_SIZE = 10
    }
}
