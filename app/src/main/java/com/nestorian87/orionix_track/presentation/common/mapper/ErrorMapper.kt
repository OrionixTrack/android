package com.nestorian87.orionix_track.presentation.common.mapper

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.nestorian87.orionix_track.R
import com.nestorian87.orionix_track.domain.error.AppError

@Composable
fun AppError.toDefaultText(): String {
    return stringResource(toMessageResId())
}

fun AppError.toMessageResId(): Int {
    return when (this) {
        AppError.Auth.InvalidCredentials -> R.string.error_login_invalid_credentials
        AppError.Auth.Forbidden -> R.string.error_forbidden
        AppError.Network.Timeout -> R.string.error_network_timeout
        AppError.Network.Unavailable -> R.string.error_network_unavailable
        AppError.Validation.InvalidEmailFormat -> R.string.error_login_invalid_email
        is AppError.Server -> R.string.error_server
        AppError.Unknown -> R.string.error_unexpected
    }
}
