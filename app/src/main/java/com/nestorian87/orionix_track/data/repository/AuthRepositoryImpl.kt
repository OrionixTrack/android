package com.nestorian87.orionix_track.data.repository

import com.nestorian87.orionix_track.data.local.SessionLocalDataSource
import com.nestorian87.orionix_track.data.mapper.toDomain
import com.nestorian87.orionix_track.data.remote.api.AuthApi
import com.nestorian87.orionix_track.data.remote.dto.ForgotPasswordRequestDto
import com.nestorian87.orionix_track.data.remote.dto.LoginRequestDto
import com.nestorian87.orionix_track.data.remote.dto.LoginResponseDto
import com.nestorian87.orionix_track.data.remote.safeApiCall
import com.nestorian87.orionix_track.data.remote.safeApiResponse
import com.nestorian87.orionix_track.domain.model.AuthSession
import com.nestorian87.orionix_track.domain.repository.AuthRepository
import com.nestorian87.orionix_track.domain.result.AppResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val sessionLocalDataSource: SessionLocalDataSource
) : AuthRepository {
    override val session: Flow<AuthSession?> =
        sessionLocalDataSource.session

    override suspend fun login(email: String, password: String): AppResult<AuthSession> {
        return saveSessionResult(
            safeApiCall {
                authApi.login(
                    LoginRequestDto(
                        email = email,
                        password = password
                    )
                )
            }
        )
    }

    override suspend fun forgotPassword(email: String): AppResult<Unit> {
        return when (
            val result = safeApiResponse {
                authApi.forgotPassword(ForgotPasswordRequestDto(email = email))
            }
        ) {
            is AppResult.Success -> AppResult.Success(Unit)
            is AppResult.Failure -> result
        }
    }

    override suspend fun logout() {
        sessionLocalDataSource.clear()
    }

    private suspend fun saveSessionResult(
        result: AppResult<LoginResponseDto>
    ): AppResult<AuthSession> {
        return when (result) {
            is AppResult.Success -> {
                val session = result.data.toDomain()
                sessionLocalDataSource.save(session)
                AppResult.Success(session)
            }
            is AppResult.Failure -> result
        }
    }
}
