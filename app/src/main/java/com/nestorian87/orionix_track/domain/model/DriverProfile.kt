package com.nestorian87.orionix_track.domain.model

data class DriverProfile(
    val id: Long,
    val email: String,
    val name: String,
    val surname: String,
    val language: AppLanguage,
    val company: Company
)

