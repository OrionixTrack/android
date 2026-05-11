package com.nestorian87.orionix_track.presentation.auth.forgot

import com.nestorian87.orionix_track.domain.error.AppError

data class ForgotPasswordUiState(
    val email: String = "",
    val isLoading: Boolean = false,
    val isSent: Boolean = false,
    val showRequiredFieldsError: Boolean = false,
    val error: AppError? = null
)
