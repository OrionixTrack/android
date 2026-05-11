package com.nestorian87.orionix_track.presentation.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nestorian87.orionix_track.domain.result.AppResult
import com.nestorian87.orionix_track.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email, error = null, showRequiredFieldsError = false) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password, error = null, showRequiredFieldsError = false) }
    }

    fun login() {
        val currentState = _uiState.value
        val normalizedEmail = currentState.email.trim()

        if (normalizedEmail.isBlank() || currentState.password.isBlank()) {
            _uiState.update { it.copy(error = null, showRequiredFieldsError = true) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, showRequiredFieldsError = false) }
            when (val result = loginUseCase(normalizedEmail, currentState.password)) {
                is AppResult.Success -> _uiState.update { it.copy(isLoading = false, error = null) }
                is AppResult.Failure -> _uiState.update {
                    it.copy(isLoading = false, error = result.error)
                }
            }
        }
    }
}
