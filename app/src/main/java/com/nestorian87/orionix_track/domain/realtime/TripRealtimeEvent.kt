package com.nestorian87.orionix_track.domain.realtime

import com.nestorian87.orionix_track.domain.model.CurrentTelemetry
import com.nestorian87.orionix_track.domain.model.TripStatus

sealed interface TripRealtimeEvent {
    data object Connected : TripRealtimeEvent
    data object Reconnected : TripRealtimeEvent
    data object Disconnected : TripRealtimeEvent

    data class TelemetryUpdated(
        val tripId: Long,
        val telemetry: CurrentTelemetry
    ) : TripRealtimeEvent

    data class StatusChanged(
        val tripId: Long,
        val status: TripStatus
    ) : TripRealtimeEvent

    data class Error(
        val message: String?
    ) : TripRealtimeEvent
}
