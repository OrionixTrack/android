package com.nestorian87.orionix_track.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    val email: String,
    val password: String
)