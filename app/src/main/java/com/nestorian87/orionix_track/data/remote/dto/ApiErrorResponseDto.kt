package com.nestorian87.orionix_track.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiErrorResponseDto(
    val statusCode: Int? = null,
    val message: String? = null,
    val error: String? = null
)
