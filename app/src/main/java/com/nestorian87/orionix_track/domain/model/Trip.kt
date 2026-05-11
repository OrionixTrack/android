package com.nestorian87.orionix_track.domain.model

data class Trip(
    val id: Long,
    val name: String,
    val description: String?,
    val status: TripStatus,
    val plannedStart: String?,
    val actualStart: String?,
    val end: String?,
    val contactInfo: String?,
    val startAddress: String?,
    val finishAddress: String?,
    val startLatitude: Double?,
    val startLongitude: Double?,
    val finishLatitude: Double?,
    val finishLongitude: Double?,
    val driver: TripUser?,
    val vehicle: TripVehicle?,
    val createdByDispatcher: TripUser?,
    val trackPolyline: String?,
    val currentTelemetry: CurrentTelemetry?
)

data class TripUser(
    val id: Long,
    val name: String?,
    val surname: String?
)

data class TripVehicle(
    val id: Long,
    val name: String?,
    val licensePlate: String?,
    val brand: String?,
    val model: String?
)

data class CurrentTelemetry(
    val latitude: Double?,
    val longitude: Double?,
    val speed: Double?,
    val datetime: String?,
    val temperature: Double?,
    val humidity: Double?
)
