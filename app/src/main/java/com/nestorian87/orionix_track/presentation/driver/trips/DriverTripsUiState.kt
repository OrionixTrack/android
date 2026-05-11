package com.nestorian87.orionix_track.presentation.driver.trips

import com.nestorian87.orionix_track.domain.error.AppError
import com.nestorian87.orionix_track.domain.model.Trip

data class DriverTripsUiState(
    val activeTrip: Trip? = null,
    val activeTripLoading: Boolean = true,
    val activeTripError: AppError? = null,
    val assignedTrips: List<Trip> = emptyList(),
    val assignedTripsLoading: Boolean = true,
    val assignedTripsAppending: Boolean = false,
    val assignedTripsError: AppError? = null,
    val searchQuery: String = "",
    val hasMoreAssignedTrips: Boolean = true,
    val historyTrips: List<Trip> = emptyList(),
    val historyTripsLoading: Boolean = true,
    val historyTripsAppending: Boolean = false,
    val historyTripsError: AppError? = null,
    val historySearchQuery: String = "",
    val hasMoreHistoryTrips: Boolean = true
)
