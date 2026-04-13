package com.nestorian87.orionix_track.data.remote

import com.nestorian87.orionix_track.domain.error.AppError
import com.nestorian87.orionix_track.domain.result.AppResult
import java.io.IOException
import java.net.SocketTimeoutException
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException

suspend fun <T> safeApiCall(
    call: suspend () -> T
): AppResult<T> {
    return try {
        AppResult.Success(call())
    } catch (throwable: Throwable) {
        AppResult.Failure(throwable.toAppError())
    }
}

private fun Throwable.toAppError(): AppError {
    return when (this) {
        is CancellationException -> throw this
        is SocketTimeoutException -> AppError.Network.Timeout
        is IOException -> AppError.Network.Unavailable
        is HttpException -> when (code()) {
            401 -> AppError.Auth.InvalidCredentials
            403 -> AppError.Auth.Forbidden
            else -> AppError.Server(code = code())
        }
        else -> AppError.Unknown
    }
}
