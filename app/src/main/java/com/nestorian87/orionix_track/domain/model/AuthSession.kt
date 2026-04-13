package com.nestorian87.orionix_track.domain.model

data class AuthSession(
    val accessToken: String,
    val driver: DriverProfile
)
