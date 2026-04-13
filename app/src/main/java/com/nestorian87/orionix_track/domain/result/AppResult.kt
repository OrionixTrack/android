package com.nestorian87.orionix_track.domain.result

import com.nestorian87.orionix_track.domain.error.AppError

sealed interface AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>
    data class Failure(val error: AppError) : AppResult<Nothing>
}
