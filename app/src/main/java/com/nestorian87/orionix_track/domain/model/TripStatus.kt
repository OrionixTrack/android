package com.nestorian87.orionix_track.domain.model

enum class TripStatus(val raw: String) {
    PLANNED("planned"),
    IN_PROGRESS("in_progress"),
    COMPLETED("completed"),
    CANCELLED("cancelled");

    companion object {
        fun fromRaw(raw: String): TripStatus? = entries.find { it.raw == raw.lowercase() }
    }
}
