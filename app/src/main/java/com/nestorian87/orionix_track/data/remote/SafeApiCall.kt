package com.nestorian87.orionix_track.data.remote

import com.nestorian87.orionix_track.data.remote.dto.ApiErrorResponseDto
import com.nestorian87.orionix_track.domain.error.AppError
import com.nestorian87.orionix_track.domain.result.AppResult
import java.io.IOException
import java.net.SocketTimeoutException
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import retrofit2.Response

suspend fun <T> safeApiCall(
    call: suspend () -> T
): AppResult<T> {
    return try {
        AppResult.Success(call())
    } catch (throwable: Throwable) {
        AppResult.Failure(throwable.toAppError())
    }
}

suspend fun <T> safeApiResponse(
    call: suspend () -> Response<T>
): AppResult<T?> {
    return try {
        val response = call()
        if (response.isSuccessful) {
            AppResult.Success(response.body())
        } else {
            AppResult.Failure(response.toAppError())
        }
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
            else -> AppError.Server(
                code = code(),
                message = response()?.errorBody()?.string()?.toApiErrorMessage()
            )
        }
        else -> AppError.Unknown
    }
}

private fun Response<*>.toAppError(): AppError {
    return when (code()) {
        401 -> AppError.Auth.InvalidCredentials
        403 -> AppError.Auth.Forbidden
        else -> AppError.Server(
            code = code(),
            message = errorBody()?.string()?.toApiErrorMessage()
        )
    }
}

private fun String.toApiErrorMessage(): String? {
    return runCatching {
        API_ERROR_JSON.decodeFromString(ApiErrorResponseDto.serializer(), this).message
    }.getOrNull()
}

private val API_ERROR_JSON: Json = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
}
