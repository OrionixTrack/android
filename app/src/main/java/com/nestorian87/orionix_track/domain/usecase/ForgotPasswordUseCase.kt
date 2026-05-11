package com.nestorian87.orionix_track.domain.usecase

import com.nestorian87.orionix_track.domain.error.AppError
import com.nestorian87.orionix_track.domain.repository.AuthRepository
import com.nestorian87.orionix_track.domain.result.AppResult
import javax.inject.Inject

class ForgotPasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String): AppResult<Unit> {
        val normalizedEmail = email.trim()
        if (!EMAIL_REGEX.matches(normalizedEmail)) {
            return AppResult.Failure(AppError.Validation.InvalidEmailFormat)
        }
        return authRepository.forgotPassword(normalizedEmail)
    }

    companion object {
        private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
    }
}
