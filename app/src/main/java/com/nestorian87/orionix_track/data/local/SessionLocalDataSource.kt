package com.nestorian87.orionix_track.data.local

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.nestorian87.orionix_track.data.local.dto.DriverSessionDto
import com.nestorian87.orionix_track.data.mapper.toDomain
import com.nestorian87.orionix_track.data.mapper.toDriverSessionDto
import com.nestorian87.orionix_track.domain.model.AuthSession
import com.nestorian87.orionix_track.domain.model.DriverProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.sessionDataStore by preferencesDataStore(name = "session_store")

private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
private val DRIVER_KEY = stringPreferencesKey("driver")

@Singleton
class SessionLocalDataSource @Inject constructor(
    private val appContext: Context,
    private val json: Json
) {
    val session: Flow<AuthSession?> = appContext.sessionDataStore.data
        .catch { error ->
            if (error is IOException) emit(emptyPreferences()) else throw error
        }
        .map { it.toSessionOrNull() }

    suspend fun save(session: AuthSession) {
        appContext.sessionDataStore.edit { prefs ->
            prefs[ACCESS_TOKEN_KEY] = session.accessToken
            prefs[DRIVER_KEY] = json.encodeToString(DriverSessionDto.serializer(), session.toDriverSessionDto())
        }
    }

    suspend fun saveDriver(driver: DriverProfile) {
        appContext.sessionDataStore.edit { prefs ->
            val accessToken = prefs[ACCESS_TOKEN_KEY] ?: return@edit
            prefs[ACCESS_TOKEN_KEY] = accessToken
            prefs[DRIVER_KEY] = json.encodeToString(
                DriverSessionDto.serializer(),
                AuthSession(accessToken = accessToken, driver = driver).toDriverSessionDto()
            )
        }
    }

    suspend fun clear() {
        appContext.sessionDataStore.edit { it.clear() }
    }

    private fun Preferences.toSessionOrNull(): AuthSession? {
        val accessToken = this[ACCESS_TOKEN_KEY] ?: return null
        val driverJson = this[DRIVER_KEY] ?: return null
        val driver = runCatching {
            json.decodeFromString(DriverSessionDto.serializer(), driverJson).toDomain()
        }.getOrNull() ?: return null
        return AuthSession(accessToken = accessToken, driver = driver)
    }
}
