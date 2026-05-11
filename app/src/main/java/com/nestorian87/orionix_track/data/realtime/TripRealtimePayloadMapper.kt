package com.nestorian87.orionix_track.data.realtime

import com.nestorian87.orionix_track.data.realtime.dto.TelemetryUpdateDto
import com.nestorian87.orionix_track.data.realtime.dto.TripStatusUpdateDto
import com.nestorian87.orionix_track.data.realtime.dto.TripSubscriptionDto
import com.nestorian87.orionix_track.domain.model.CurrentTelemetry
import com.nestorian87.orionix_track.domain.model.TripStatus
import com.nestorian87.orionix_track.domain.realtime.TripRealtimeEvent
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.longOrNull
import kotlinx.serialization.json.jsonObject
import org.json.JSONArray
import org.json.JSONObject

internal fun Long.toSocketTripSubscription(
    json: Json
): JSONObject {
    return json.encodeToJsonElement(
        TripSubscriptionDto.serializer(),
        TripSubscriptionDto(tripId = this)
    ).toJsonObject()
}

internal fun Any.toTelemetryEvent(
    json: Json
): TripRealtimeEvent.TelemetryUpdated? {
    val payload = toJsonString(json) ?: return null
    val dto = runCatching {
        json.decodeFromString<TelemetryUpdateDto>(payload)
    }.getOrNull() ?: return null

    return TripRealtimeEvent.TelemetryUpdated(
        tripId = dto.tripId,
        telemetry = CurrentTelemetry(
            latitude = dto.latitude,
            longitude = dto.longitude,
            speed = dto.speed,
            datetime = dto.datetime,
            temperature = dto.temperature,
            humidity = dto.humidity
        )
    )
}

internal fun Any.toStatusEvent(
    json: Json
): TripRealtimeEvent.StatusChanged? {
    val payload = toJsonString(json) ?: return null
    val dto = runCatching {
        json.decodeFromString<TripStatusUpdateDto>(payload)
    }.getOrNull() ?: return null
    val status = TripStatus.fromRaw(dto.status) ?: return null
    return TripRealtimeEvent.StatusChanged(tripId = dto.tripId, status = status)
}

private fun Any.toJsonString(json: Json): String? {
    return when (this) {
        is JSONObject -> this.toString()
        is String -> this
        is JsonObject -> this.toString()
        else -> runCatching {
            json.encodeToString(json.parseToJsonElement(toString()).jsonObject)
        }.getOrNull()
    }
}

private fun JsonElement.toJsonObject(): JSONObject {
    return requireNotNull(toOrgJsonValue() as? JSONObject)
}

private fun JsonElement.toOrgJsonValue(): Any {
    return when (this) {
        is JsonObject -> JSONObject().also { jsonObject ->
            entries.forEach { (key, value) ->
                jsonObject.put(key, value.toOrgJsonValue())
            }
        }
        is JsonArray -> JSONArray().also { jsonArray ->
            forEach { value ->
                jsonArray.put(value.toOrgJsonValue())
            }
        }
        is JsonNull -> JSONObject.NULL
        is JsonPrimitive -> toOrgJsonPrimitive()
    }
}

private fun JsonPrimitive.toOrgJsonPrimitive(): Any {
    if (isString) return content
    booleanOrNull?.let { return it }
    longOrNull?.let { return it }
    doubleOrNull?.let { return it }
    return content
}
