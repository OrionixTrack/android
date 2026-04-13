package com.nestorian87.orionix_track.data.mapper

import com.nestorian87.orionix_track.data.remote.dto.DriverProfileDto
import com.nestorian87.orionix_track.domain.model.AppLanguage
import com.nestorian87.orionix_track.domain.model.Company
import com.nestorian87.orionix_track.domain.model.DriverProfile

fun DriverProfileDto.toDomain(): DriverProfile = DriverProfile(
    id = id,
    email = email,
    name = name,
    surname = surname,
    language = when (language.lowercase()) {
        "uk" -> AppLanguage.UK
        else -> AppLanguage.EN
    },
    company = Company(id = company.id, name = company.name)
)
