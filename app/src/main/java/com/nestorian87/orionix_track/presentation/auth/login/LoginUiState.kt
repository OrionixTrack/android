package com.nestorian87.orionix_track.presentation.auth.login

import com.nestorian87.orionix_track.domain.error.AppError

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: AppError? = null,
    val showRequiredFieldsError: Boolean = false
)
