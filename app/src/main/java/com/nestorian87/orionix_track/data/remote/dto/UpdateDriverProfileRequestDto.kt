package com.nestorian87.orionix_track.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UpdateDriverProfileRequestDto(
    val name: String,
    val surname: String
)
