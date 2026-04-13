package com.nestorian87.orionix_track.presentation.common.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nestorian87.orionix_track.domain.repository.AuthRepository
import com.nestorian87.orionix_track.presentation.theme.AppThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class RootViewModel @Inject constructor(
    val authRepository: AuthRepository
) : ViewModel() {
    private val themeMode = MutableStateFlow(AppThemeMode.SYSTEM)
    private val isLogoutLoading = MutableStateFlow(false)

    val uiState: StateFlow<RootUiState> = combine(
        authRepository.session
            .map { session ->
                if (session == null) RootAuthState.Unauthenticated
                else RootAuthState.Authenticated(session)
            }
            .catch { emit(RootAuthState.Unauthenticated) },
        themeMode,
        isLogoutLoading
    ) { authState, themeMode, isLogoutLoading ->
        RootUiState(
            authState = authState,
            themeMode = themeMode,
            isLogoutLoading = isLogoutLoading
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = RootUiState()
    )

    fun onThemeModeChange(themeMode: AppThemeMode) {
        this.themeMode.update { themeMode }
    }

    fun logout() {
        if (isLogoutLoading.value) return
        viewModelScope.launch {
            isLogoutLoading.value = true
            try {
                authRepository.logout()
            } catch (exception: CancellationException) {
                throw exception
            } finally {
                isLogoutLoading.value = false
            }
        }
    }
}
