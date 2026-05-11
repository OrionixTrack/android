package com.nestorian87.orionix_track.data.mapper

import com.nestorian87.orionix_track.data.remote.dto.CurrentTelemetryDto
import com.nestorian87.orionix_track.data.remote.dto.TripDto
import com.nestorian87.orionix_track.data.remote.dto.TripUserDto
import com.nestorian87.orionix_track.data.remote.dto.TripVehicleDto
import com.nestorian87.orionix_track.domain.model.CurrentTelemetry
import com.nestorian87.orionix_track.domain.model.Trip
import com.nestorian87.orionix_track.domain.model.TripStatus
import com.nestorian87.orionix_track.domain.model.TripUser
import com.nestorian87.orionix_track.domain.model.TripVehicle

fun TripDto.toDomain(): Trip {
    val status = status?.let(TripStatus::fromRaw) ?: TripStatus.PLANNED
    return Trip(
        id = id,
        name = name.orEmpty(),
        description = description,
        status = status,
        plannedStart = plannedStart,
        actualStart = actualStart,
        end = end,
        contactInfo = contactInfo,
        startAddress = startAddress,
        finishAddress = finishAddress,
        startLatitude = startLatitude,
        startLongitude = startLongitude,
        finishLatitude = finishLatitude,
        finishLongitude = finishLongitude,
        driver = driver?.toDomain(),
        vehicle = vehicle?.toDomain(),
        createdByDispatcher = createdByDispatcher?.toDomain(),
        trackPolyline = trackPolyline,
        currentTelemetry = currentTelemetry?.toDomain()
    )
}

private fun TripUserDto.toDomain(): TripUser = TripUser(
    id = id,
    name = name,
    surname = surname
)

private fun TripVehicleDto.toDomain(): TripVehicle = TripVehicle(
    id = id,
    name = name,
    licensePlate = licensePlate,
    brand = brand,
    model = model
)

private fun CurrentTelemetryDto.toDomain(): CurrentTelemetry = CurrentTelemetry(
    latitude = latitude,
    longitude = longitude,
    speed = speed,
    datetime = datetime,
    temperature = temperature,
    humidity = humidity
)
