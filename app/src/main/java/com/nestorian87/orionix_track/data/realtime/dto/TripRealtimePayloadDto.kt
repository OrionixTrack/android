package com.nestorian87.orionix_track.data.realtime.dto

import kotlinx.serialization.Serializable

@Serializable
data class TripSubscriptionDto(
    val tripId: Long
)

@Serializable
data class TelemetryUpdateDto(
    val tripId: Long,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val speed: Double? = null,
    val datetime: String? = null,
    val temperature: Double? = null,
    val humidity: Double? = null
)

@Serializable
data class TripStatusUpdateDto(
    val tripId: Long,
    val status: String
)
