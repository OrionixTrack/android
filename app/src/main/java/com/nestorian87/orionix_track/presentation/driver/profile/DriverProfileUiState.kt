package com.nestorian87.orionix_track.presentation.driver.profile

import com.nestorian87.orionix_track.domain.error.AppError
import com.nestorian87.orionix_track.domain.model.DriverProfile

data class DriverProfileUiState(
    val profile: DriverProfile? = null,
    val name: String = "",
    val surname: String = "",
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val error: AppError? = null,
    val validationError: DriverProfileValidationError? = null,
    val isSaved: Boolean = false
) {
    val hasChanges: Boolean
        get() = profile != null &&
            (name != profile.name || surname != profile.surname)
}

enum class DriverProfileValidationError {
    EmptyRequiredFields
}
