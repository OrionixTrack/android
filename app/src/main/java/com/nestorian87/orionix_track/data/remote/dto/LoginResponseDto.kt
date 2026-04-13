package com.nestorian87.orionix_track.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponseDto(
    @SerialName("access_token")
    val accessToken: String,
    val driver: DriverProfileDto
)