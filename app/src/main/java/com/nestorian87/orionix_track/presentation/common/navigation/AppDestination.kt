package com.nestorian87.orionix_track.presentation.common.navigation

object AppGraphRoute {
    const val SPLASH = "splash"
    const val AUTH = "auth"
    const val MAIN = "main"
}

object AppDestination {
    const val LOGIN = "auth/login"
    const val FORGOT_PASSWORD = "auth/forgot-password"
    const val TRIPS = "main/trips"
    const val TRIP_ID_ARG = "tripId"
    const val TRIP_DETAILS = "main/trips/{$TRIP_ID_ARG}"

    fun tripDetails(tripId: Long): String = "main/trips/$tripId"
}
