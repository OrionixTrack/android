package com.nestorian87.orionix_track.domain.repository

import com.nestorian87.orionix_track.domain.model.DriverProfile
import com.nestorian87.orionix_track.domain.result.AppResult

interface ProfileRepository {
    suspend fun getDriverProfile(): AppResult<DriverProfile>

    suspend fun updateDriverProfile(
        name: String,
        surname: String
    ): AppResult<DriverProfile>
}
