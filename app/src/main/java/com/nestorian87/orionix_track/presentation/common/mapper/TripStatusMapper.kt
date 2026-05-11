package com.nestorian87.orionix_track.presentation.common.mapper

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.nestorian87.orionix_track.R
import com.nestorian87.orionix_track.domain.model.TripStatus

@Composable
fun TripStatus.toLocalizedText(): String {
    return stringResource(
        when (this) {
            TripStatus.PLANNED -> R.string.trip_status_planned
            TripStatus.IN_PROGRESS -> R.string.trip_status_in_progress
            TripStatus.COMPLETED -> R.string.trip_status_completed
            TripStatus.CANCELLED -> R.string.trip_status_cancelled
        }
    )
}
