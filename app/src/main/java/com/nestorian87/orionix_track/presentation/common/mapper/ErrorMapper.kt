package com.nestorian87.orionix_track.presentation.common.mapper

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.nestorian87.orionix_track.R
import com.nestorian87.orionix_track.domain.error.AppError

@Composable
fun AppError.toDefaultText(): String {
    val resId = when (this) {
        is AppError.Network -> R.string.error_network_unavailable
        else -> R.string.error_unexpected
    }
    return stringResource(resId)
}