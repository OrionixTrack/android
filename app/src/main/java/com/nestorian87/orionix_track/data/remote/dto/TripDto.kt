package com.nestorian87.orionix_track.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class TripDto(
    val id: Long,
    val name: String? = null,
    val description: String? = null,
    val status: String? = null,
    val plannedStart: String? = null,
    val actualStart: String? = null,
    val end: String? = null,
    val contactInfo: String? = null,
    val startAddress: String? = null,
    val finishAddress: String? = null,
    val startLatitude: Double? = null,
    val startLongitude: Double? = null,
    val finishLatitude: Double? = null,
    val finishLongitude: Double? = null,
    val driver: TripUserDto? = null,
    val vehicle: TripVehicleDto? = null,
    val createdByDispatcher: TripUserDto? = null,
    val trackPolyline: String? = null,
    val currentTelemetry: CurrentTelemetryDto? = null
)

@Serializable
data class TripUserDto(
    val id: Long,
    val name: String? = null,
    val surname: String? = null
)

@Serializable
data class TripVehicleDto(
    val id: Long,
    val name: String? = null,
    val licensePlate: String? = null,
    val brand: String? = null,
    val model: String? = null
)

@Serializable
data class CurrentTelemetryDto(
    val latitude: Double? = null,
    val longitude: Double? = null,
    val speed: Double? = null,
    val datetime: String? = null,
    val temperature: Double? = null,
    val humidity: Double? = null
)
