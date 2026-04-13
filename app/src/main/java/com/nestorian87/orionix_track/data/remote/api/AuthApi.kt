package com.nestorian87.orionix_track.data.remote.api

import com.nestorian87.orionix_track.data.remote.dto.LoginRequestDto
import com.nestorian87.orionix_track.data.remote.dto.LoginResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/driver/login")
    suspend fun login(@Body request: LoginRequestDto): LoginResponseDto
}
