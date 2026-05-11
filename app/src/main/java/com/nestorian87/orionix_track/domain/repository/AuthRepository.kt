package com.nestorian87.orionix_track.domain.repository

import com.nestorian87.orionix_track.domain.model.AuthSession
import com.nestorian87.orionix_track.domain.result.AppResult
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val session: Flow<AuthSession?>

    suspend fun login(email: String, password: String): AppResult<AuthSession>

    suspend fun forgotPassword(email: String): AppResult<Unit>

    suspend fun logout()
}
