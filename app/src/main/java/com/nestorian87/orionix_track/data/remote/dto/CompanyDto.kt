package com.nestorian87.orionix_track.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CompanyDto(
    val id: Long,
    val name: String
)