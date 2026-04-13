package com.nestorian87.orionix_track.data.mapper

import com.nestorian87.orionix_track.data.local.dto.DriverSessionDto
import com.nestorian87.orionix_track.domain.model.AppLanguage
import com.nestorian87.orionix_track.domain.model.AuthSession
import com.nestorian87.orionix_track.domain.model.Company
import com.nestorian87.orionix_track.domain.model.DriverProfile

internal fun DriverSessionDto.toDomain(): DriverProfile = DriverProfile(
    id = id,
    email = email,
    name = name,
    surname = surname,
    language = AppLanguage.entries.find { it.code == language } ?: AppLanguage.EN,
    company = Company(id = companyId, name = companyName)
)

internal fun AuthSession.toDriverSessionDto(): DriverSessionDto = DriverSessionDto(
    id = driver.id,
    email = driver.email,
    name = driver.name,
    surname = driver.surname,
    language = driver.language.code,
    companyId = driver.company.id,
    companyName = driver.company.name
)
