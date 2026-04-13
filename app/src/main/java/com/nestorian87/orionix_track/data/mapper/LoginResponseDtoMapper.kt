package com.nestorian87.orionix_track.data.mapper

import com.nestorian87.orionix_track.data.remote.dto.LoginResponseDto
import com.nestorian87.orionix_track.domain.model.AuthSession

fun LoginResponseDto.toDomain(): AuthSession = AuthSession(
    accessToken = accessToken,
    driver = driver.toDomain()
)
