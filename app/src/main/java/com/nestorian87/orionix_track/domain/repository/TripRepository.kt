package com.nestorian87.orionix_track.domain.repository

import com.nestorian87.orionix_track.domain.model.Trip
import com.nestorian87.orionix_track.domain.result.AppResult

interface TripRepository {
    suspend fun getAssignedTrips(
        limit: Int,
        offset: Int,
        search: String
    ): AppResult<List<Trip>>

    suspend fun getTripHistory(
        limit: Int,
        offset: Int,
        search: String
    ): AppResult<List<Trip>>

    suspend fun getActiveTrip(): AppResult<Trip?>

    suspend fun getTripDetails(tripId: Long): AppResult<Trip>

    suspend fun startTrip(tripId: Long): AppResult<Trip>

    suspend fun endTrip(tripId: Long): AppResult<Trip>
}
