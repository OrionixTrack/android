package com.nestorian87.orionix_track.presentation.driver.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nestorian87.orionix_track.domain.repository.ProfileRepository
import com.nestorian87.orionix_track.domain.result.AppResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class DriverProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(DriverProfileUiState())
    val uiState: StateFlow<DriverProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun onNameChanged(value: String) {
        _uiState.update {
            it.copy(
                name = value,
                validationError = null,
                isSaved = false
            )
        }
    }

    fun onSurnameChanged(value: String) {
        _uiState.update {
            it.copy(
                surname = value,
                validationError = null,
                isSaved = false
            )
        }
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    validationError = null,
                    isSaved = false
                )
            }
            when (val result = profileRepository.getDriverProfile()) {
                is AppResult.Success -> {
                    val profile = result.data
                    _uiState.update {
                        it.copy(
                            profile = profile,
                            name = profile.name,
                            surname = profile.surname,
                            isLoading = false,
                            error = null
                        )
                    }
                }
                is AppResult.Failure -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.error
                        )
                    }
                }
            }
        }
    }

    fun saveProfile() {
        val currentState = _uiState.value
        val name = currentState.name.trim()
        val surname = currentState.surname.trim()

        if (name.isEmpty() || surname.isEmpty()) {
            _uiState.update {
                it.copy(
                    validationError = DriverProfileValidationError.EmptyRequiredFields,
                    isSaved = false
                )
            }
            return
        }

        if (currentState.isSaving || !currentState.hasChanges) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSaving = true,
                    error = null,
                    validationError = null,
                    isSaved = false
                )
            }
            when (val result = profileRepository.updateDriverProfile(name, surname)) {
                is AppResult.Success -> {
                    val profile = result.data
                    _uiState.update {
                        it.copy(
                            profile = profile,
                            name = profile.name,
                            surname = profile.surname,
                            isSaving = false,
                            error = null,
                            isSaved = true
                        )
                    }
                }
                is AppResult.Failure -> {
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            error = result.error
                        )
                    }
                }
            }
        }
    }
}
