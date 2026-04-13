package com.nestorian87.orionix_track.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class DriverProfileDto(
    val id: Long,
    val email: String,
    val name: String,
    val surname: String,
    val language: String,
    val company: CompanyDto
)