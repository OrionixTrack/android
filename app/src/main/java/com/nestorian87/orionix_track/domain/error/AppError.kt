package com.nestorian87.orionix_track.domain.error

sealed interface AppError {
    sealed interface Validation : AppError {
        data object InvalidEmailFormat : Validation
    }

    sealed interface Auth : AppError {
        data object InvalidCredentials : Auth
        data object Forbidden : Auth
    }

    sealed interface Network : AppError {
        data object Unavailable : Network
        data object Timeout : Network
    }

    data class Server(
        val code: Int?,
        val message: String? = null
    ) : AppError
    data object Unknown : AppError
}
