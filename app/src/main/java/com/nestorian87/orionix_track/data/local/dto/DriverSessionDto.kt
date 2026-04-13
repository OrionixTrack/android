package com.nestorian87.orionix_track.data.local.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class DriverSessionDto(
    val id: Long,
    val email: String,
    val name: String,
    val surname: String,
    val language: String,
    val companyId: Long,
    val companyName: String
)
