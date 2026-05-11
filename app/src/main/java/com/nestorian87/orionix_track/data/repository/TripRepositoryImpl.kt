package com.nestorian87.orionix_track.data.repository

import com.nestorian87.orionix_track.data.mapper.toDomain
import com.nestorian87.orionix_track.data.remote.api.TripApi
import com.nestorian87.orionix_track.data.remote.safeApiCall
import com.nestorian87.orionix_track.domain.model.Trip
import com.nestorian87.orionix_track.domain.repository.TripRepository
import com.nestorian87.orionix_track.domain.result.AppResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TripRepositoryImpl @Inject constructor(
    private val tripApi: TripApi
) : TripRepository {
    override suspend fun getAssignedTrips(
        limit: Int,
        offset: Int,
        search: String
    ): AppResult<List<Trip>> {
        return when (
            val result = safeApiCall { tripApi.getAssignedTrips(limit = limit, offset = offset, search = search) }
        ) {
            is AppResult.Success -> AppResult.Success(result.data.map { it.toDomain() })
            is AppResult.Failure -> result
        }
    }

    override suspend fun getTripHistory(
        limit: Int,
        offset: Int,
        search: String
    ): AppResult<List<Trip>> {
        return when (
            val result = safeApiCall { tripApi.getTripHistory(limit = limit, offset = offset, search = search) }
        ) {
            is AppResult.Success -> AppResult.Success(result.data.map { it.toDomain() })
            is AppResult.Failure -> result
        }
    }

    override suspend fun getActiveTrip(): AppResult<Trip?> {
        return when (val result = safeApiCall { tripApi.getActiveTrip() }) {
            is AppResult.Success -> AppResult.Success(result.data.activeTrip?.toDomain())
            is AppResult.Failure -> result
        }
    }

    override suspend fun getTripDetails(tripId: Long): AppResult<Trip> {
        return when (val result = safeApiCall { tripApi.getTripDetails(tripId = tripId) }) {
            is AppResult.Success -> AppResult.Success(result.data.toDomain())
            is AppResult.Failure -> result
        }
    }

    override suspend fun startTrip(tripId: Long): AppResult<Trip> {
        return when (val result = safeApiCall { tripApi.startTrip(tripId = tripId) }) {
            is AppResult.Success -> AppResult.Success(result.data.toDomain())
            is AppResult.Failure -> result
        }
    }

    override suspend fun endTrip(tripId: Long): AppResult<Trip> {
        return when (val result = safeApiCall { tripApi.endTrip(tripId = tripId) }) {
            is AppResult.Success -> AppResult.Success(result.data.toDomain())
            is AppResult.Failure -> result
        }
    }
}
