package com.nestorian87.orionix_track.data.repository

import com.nestorian87.orionix_track.data.local.SessionLocalDataSource
import com.nestorian87.orionix_track.data.mapper.toDomain
import com.nestorian87.orionix_track.data.remote.api.ProfileApi
import com.nestorian87.orionix_track.data.remote.dto.UpdateDriverProfileRequestDto
import com.nestorian87.orionix_track.data.remote.safeApiCall
import com.nestorian87.orionix_track.data.remote.safeApiResponse
import com.nestorian87.orionix_track.domain.model.DriverProfile
import com.nestorian87.orionix_track.domain.repository.ProfileRepository
import com.nestorian87.orionix_track.domain.result.AppResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val profileApi: ProfileApi,
    private val sessionLocalDataSource: SessionLocalDataSource
) : ProfileRepository {
    override suspend fun getDriverProfile(): AppResult<DriverProfile> {
        return when (val result = safeApiCall { profileApi.getDriverProfile() }) {
            is AppResult.Success -> {
                val profile = result.data.toDomain()
                sessionLocalDataSource.saveDriver(profile)
                AppResult.Success(profile)
            }
            is AppResult.Failure -> result
        }
    }

    override suspend fun updateDriverProfile(
        name: String,
        surname: String
    ): AppResult<DriverProfile> {
        return when (
            val result = safeApiResponse {
                profileApi.updateDriverProfile(
                    UpdateDriverProfileRequestDto(
                        name = name,
                        surname = surname
                    )
                )
            }
        ) {
            is AppResult.Success -> getDriverProfile()
            is AppResult.Failure -> result
        }
    }
}
