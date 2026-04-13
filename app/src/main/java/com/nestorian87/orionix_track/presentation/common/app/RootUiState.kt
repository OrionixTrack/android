package com.nestorian87.orionix_track.presentation.common.app

import com.nestorian87.orionix_track.domain.model.AuthSession
import com.nestorian87.orionix_track.presentation.theme.AppThemeMode

sealed interface RootAuthState {
    data object Resolving : RootAuthState
    data object Unauthenticated : RootAuthState
    data class Authenticated(val session: AuthSession) : RootAuthState
}

data class RootUiState(
    val authState: RootAuthState = RootAuthState.Resolving,
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val isLogoutLoading: Boolean = false
)
