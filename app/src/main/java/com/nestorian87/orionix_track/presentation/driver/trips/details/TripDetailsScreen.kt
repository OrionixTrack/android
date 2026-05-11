package com.nestorian87.orionix_track.presentation.driver.trips.details

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nestorian87.orionix_track.R
import com.nestorian87.orionix_track.domain.model.Trip
import com.nestorian87.orionix_track.domain.model.TripStatus
import com.nestorian87.orionix_track.presentation.common.components.OrionixBackdrop
import com.nestorian87.orionix_track.presentation.common.components.OrionixErrorPanel
import com.nestorian87.orionix_track.presentation.common.components.OrionixLoadingDialog
import com.nestorian87.orionix_track.presentation.common.components.OrionixPanel
import com.nestorian87.orionix_track.presentation.common.components.OrionixPrimaryButton
import com.nestorian87.orionix_track.presentation.common.components.OrionixSecondaryButton
import com.nestorian87.orionix_track.presentation.common.components.OrionixStatePanel
import com.nestorian87.orionix_track.presentation.common.formatter.formatIsoDateTime
import com.nestorian87.orionix_track.presentation.common.mapper.toLocalizedText
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun TripDetailsScreen(
    onBack: () -> Unit,
    viewModel: TripDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val locale = LocalConfiguration.current.locales[0]

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                TripDetailsEvent.NavigateBackToList -> onBack()
            }
        }
    }

    OrionixBackdrop {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            TripTopBar(onBack = onBack)
            when {
                uiState.isLoading -> OrionixStatePanel(
                    title = stringResource(R.string.loading_text),
                    isLoading = true
                )

                uiState.isUnavailable -> OrionixStatePanel(
                    title = stringResource(R.string.trip_unavailable),
                    isError = true
                )

                uiState.trip == null && uiState.error != null -> {
                    val error = uiState.error ?: return@Column
                    OrionixErrorPanel(error = error)
                }

                uiState.trip == null -> OrionixStatePanel(title = stringResource(R.string.error_unexpected))

                else -> {
                    val trip = uiState.trip ?: return@Column
                    TripHeroPanel(trip = trip, localeTag = locale)
                    TripActionButtons(
                        trip = trip,
                        isLoading = uiState.isActionLoading,
                        onStart = viewModel::startTrip,
                        onEnd = viewModel::endTrip
                    )
                    TripRoutePanel(trip = trip)
                    TripParticipantsPanel(trip = trip)
                    TripTelemetryPanel(trip = trip, localeTag = locale)
                    TripNavigationButton(
                        trip = trip,
                        onOpen = { lat, lng, address ->
                            val uri = Uri.parse("geo:$lat,$lng?q=$lat,$lng(${address.orEmpty()})")
                            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                        }
                    )
                    if (uiState.error != null) {
                        val error = uiState.error ?: return@Column
                        OrionixErrorPanel(error = error)
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
        OrionixLoadingDialog(
            isVisible = uiState.isActionLoading,
            message = stringResource(R.string.loading_text)
        )
    }
}

@Composable
private fun TripTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.action_back_to_trips)
                )
            }
        }
    }
}

@Composable
private fun TripHeroPanel(
    trip: Trip,
    localeTag: java.util.Locale
) {
    OrionixPanel {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = trip.name,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(12.dp))
            StatusChip(status = trip.status)
        }
        trip.description?.takeIf { it.isNotBlank() }?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        InfoRow(
            icon = Icons.Default.Timer,
            label = stringResource(R.string.trip_planned_start_label),
            value = formatIsoDateTime(trip.plannedStart, localeTag)
        )
        InfoRow(
            icon = Icons.Default.Timer,
            label = stringResource(R.string.trip_actual_start_label),
            value = formatIsoDateTime(trip.actualStart, localeTag)
        )
        if (trip.end != null) {
            InfoRow(
                icon = Icons.Default.Timer,
                label = stringResource(R.string.trip_end_label),
                value = formatIsoDateTime(trip.end, localeTag)
            )
        }
        if (!trip.contactInfo.isNullOrBlank()) {
            TripField(stringResource(R.string.trip_contact_label), trip.contactInfo)
        }
    }
}

@Composable
private fun TripRoutePanel(trip: Trip) {
    OrionixPanel {
        SectionTitle(
            icon = Icons.Default.Route,
            title = stringResource(R.string.trip_route_title)
        )
        TripField(stringResource(R.string.trip_start_address_label), trip.startAddress)
        TripField(stringResource(R.string.trip_finish_address_label), trip.finishAddress)
    }
}

@Composable
private fun TripParticipantsPanel(trip: Trip) {
    OrionixPanel {
        SectionTitle(
            icon = Icons.Default.LocalShipping,
            title = stringResource(R.string.trip_team_title)
        )
        TripField(
            label = stringResource(R.string.trip_driver_label),
            value = listOfNotNull(trip.driver?.name, trip.driver?.surname).joinToString(" ")
        )
        TripField(
            label = stringResource(R.string.trip_vehicle_label),
            value = listOfNotNull(
                trip.vehicle?.name,
                trip.vehicle?.brand,
                trip.vehicle?.model,
                trip.vehicle?.licensePlate
            ).joinToString(" • ")
        )
        TripField(
            label = stringResource(R.string.trip_dispatcher_label),
            value = listOfNotNull(trip.createdByDispatcher?.name, trip.createdByDispatcher?.surname).joinToString(" ")
        )
    }
}

@Composable
private fun TripTelemetryPanel(
    trip: Trip,
    localeTag: java.util.Locale
) {
    OrionixPanel {
        SectionTitle(
            icon = Icons.Default.Sensors,
            title = stringResource(R.string.trip_telemetry_label)
        )
        if (trip.currentTelemetry == null) {
            TripField(stringResource(R.string.trip_telemetry_empty), null)
        } else {
            TripField(stringResource(R.string.trip_speed_label), trip.currentTelemetry.speed?.toString())
            TripField(stringResource(R.string.trip_temperature_label), trip.currentTelemetry.temperature?.toString())
            TripField(stringResource(R.string.trip_humidity_label), trip.currentTelemetry.humidity?.toString())
            TripField(
                stringResource(R.string.trip_telemetry_time_label),
                formatIsoDateTime(trip.currentTelemetry.datetime, localeTag)
            )
        }
    }
}

@Composable
private fun TripNavigationButton(
    trip: Trip,
    onOpen: (Double, Double, String?) -> Unit
) {
    val lat = trip.finishLatitude
    val lng = trip.finishLongitude
    if (trip.status != TripStatus.IN_PROGRESS || lat == null || lng == null) return

    OrionixSecondaryButton(
        text = stringResource(R.string.action_open_navigation),
        onClick = {
            onOpen(lat, lng, trip.finishAddress)
        }
    )
}

@Composable
private fun TripActionButtons(
    trip: Trip,
    isLoading: Boolean,
    onStart: () -> Unit,
    onEnd: () -> Unit
) {
    when (trip.status) {
        TripStatus.PLANNED -> OrionixPrimaryButton(
            text = stringResource(R.string.action_start_trip),
            onClick = onStart,
            enabled = !isLoading
        )

        TripStatus.IN_PROGRESS -> OrionixPrimaryButton(
            text = stringResource(R.string.action_end_trip),
            onClick = onEnd,
            enabled = !isLoading
        )

        TripStatus.COMPLETED, TripStatus.CANCELLED -> Unit
    }
}

@Composable
private fun SectionTitle(
    icon: ImageVector,
    title: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String?
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )
        TripField(label = label, value = value)
    }
}

@Composable
private fun StatusChip(status: TripStatus) {
    val colors = MaterialTheme.colorScheme
    Surface(
        shape = CircleShape,
        color = when (status) {
            TripStatus.PLANNED -> colors.secondaryContainer.copy(alpha = 0.72f)
            TripStatus.IN_PROGRESS -> colors.tertiary.copy(alpha = 0.16f)
            TripStatus.COMPLETED -> colors.primary.copy(alpha = 0.16f)
            TripStatus.CANCELLED -> colors.error.copy(alpha = 0.14f)
        },
        contentColor = when (status) {
            TripStatus.CANCELLED -> colors.error
            TripStatus.IN_PROGRESS -> colors.tertiary
            else -> colors.onSurface
        }
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            text = status.toLocalizedText(),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun TripField(
    label: String,
    value: String?
) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value?.takeIf { it.isNotBlank() } ?: "—",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
