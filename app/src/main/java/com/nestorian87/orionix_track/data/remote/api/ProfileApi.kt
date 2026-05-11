package com.nestorian87.orionix_track.data.remote.api

import com.nestorian87.orionix_track.data.remote.dto.DriverProfileDto
import com.nestorian87.orionix_track.data.remote.dto.UpdateDriverProfileRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

interface ProfileApi {
    @GET("driver/profile")
    suspend fun getDriverProfile(): DriverProfileDto

    @PUT("profile/driver")
    suspend fun updateDriverProfile(
        @Body request: UpdateDriverProfileRequestDto
    ): Response<Unit>
}
