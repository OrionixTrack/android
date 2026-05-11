package com.nestorian87.orionix_track.presentation.common.formatter

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

fun formatIsoDateTime(
    dateTime: String?,
    locale: Locale
): String {
    if (dateTime.isNullOrBlank()) return "—"
    return runCatching {
        val instant = Instant.parse(dateTime)
        DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
            .withLocale(locale)
            .withZone(ZoneId.systemDefault())
            .format(instant)
    }.getOrElse { dateTime }
}
