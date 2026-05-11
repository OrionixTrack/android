package com.nestorian87.orionix_track.data.realtime

import com.nestorian87.orionix_track.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RealtimeUrlProvider @Inject constructor() {
    fun trackingUrl(): String {
        return BuildConfig.API_BASE_URL.trimEnd('/') + TRACKING_NAMESPACE
    }

    companion object {
        private const val TRACKING_NAMESPACE = "/tracking"
    }
}
