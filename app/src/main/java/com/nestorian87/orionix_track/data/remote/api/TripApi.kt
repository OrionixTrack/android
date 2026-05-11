package com.nestorian87.orionix_track.data.remote.api

import com.nestorian87.orionix_track.data.remote.dto.ActiveTripResponseDto
import com.nestorian87.orionix_track.data.remote.dto.TripDto
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface TripApi {
    @GET("driver/trips")
    suspend fun getAssignedTrips(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("search") search: String
    ): List<TripDto>

    @GET("driver/trips/history")
    suspend fun getTripHistory(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("search") search: String
    ): List<TripDto>

    @GET("driver/trips/active")
    suspend fun getActiveTrip(): ActiveTripResponseDto

    @GET("driver/trips/{id}")
    suspend fun getTripDetails(
        @Path("id") tripId: Long
    ): TripDto

    @POST("driver/trips/{id}/start")
    suspend fun startTrip(
        @Path("id") tripId: Long
    ): TripDto

    @POST("driver/trips/{id}/end")
    suspend fun endTrip(
        @Path("id") tripId: Long
    ): TripDto
}
