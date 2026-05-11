package com.nestorian87.orionix_track.presentation.driver.trips.details

import com.nestorian87.orionix_track.domain.error.AppError
import com.nestorian87.orionix_track.domain.model.Trip

data class TripDetailsUiState(
    val trip: Trip? = null,
    val isLoading: Boolean = true,
    val isActionLoading: Boolean = false,
    val isUnavailable: Boolean = false,
    val error: AppError? = null
)

sealed interface TripDetailsEvent {
    data object NavigateBackToList : TripDetailsEvent
}
