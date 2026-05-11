package com.nestorian87.orionix_track.presentation.driver.trips.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.nestorian87.orionix_track.R
import com.nestorian87.orionix_track.domain.model.Trip
import com.nestorian87.orionix_track.domain.model.TripStatus
import com.nestorian87.orionix_track.presentation.common.components.OrionixPanel
import com.nestorian87.orionix_track.presentation.common.formatter.formatIsoDateTime
import com.nestorian87.orionix_track.presentation.common.mapper.toLocalizedText

@Composable
fun TripCard(
    trip: Trip,
    onClick: () -> Unit
) {
    val locale = LocalConfiguration.current.locales[0]
    OrionixPanel(
        modifier = Modifier.clickable(
            role = Role.Button,
            onClick = onClick
        )
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = trip.name,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                TripStatusChip(status = trip.status)
            }
            Text(
                text = listOfNotNull(
                    trip.startAddress?.compactAddress(),
                    trip.finishAddress?.compactAddress()
                ).joinToString(" → ").ifBlank {
                    stringResource(R.string.trip_route_unavailable)
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = formatIsoDateTime(trip.plannedStart, locale),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun String.compactAddress(): String? {
    return split(',')
        .firstOrNull()
        ?.trim()
        ?.takeIf { it.isNotBlank() }
}

@Composable
fun TripStatusChip(status: TripStatus) {
    val colors = MaterialTheme.colorScheme
    val containerColor = when (status) {
        TripStatus.PLANNED -> colors.primary.copy(alpha = 0.12f)
        TripStatus.IN_PROGRESS -> colors.tertiary.copy(alpha = 0.16f)
        TripStatus.COMPLETED -> colors.surfaceVariant
        TripStatus.CANCELLED -> colors.error.copy(alpha = 0.14f)
    }
    val contentColor = when (status) {
        TripStatus.PLANNED -> colors.primary
        TripStatus.IN_PROGRESS -> colors.tertiary
        TripStatus.COMPLETED -> colors.onSurfaceVariant
        TripStatus.CANCELLED -> colors.error
    }

    Surface(
        shape = RoundedCornerShape(999.dp),
        color = containerColor
    ) {
        Text(
            text = status.toLocalizedText(),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelSmall,
            color = contentColor
        )
    }
}
