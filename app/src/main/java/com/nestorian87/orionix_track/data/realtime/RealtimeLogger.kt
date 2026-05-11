package com.nestorian87.orionix_track.data.realtime

import android.util.Log
import com.nestorian87.orionix_track.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RealtimeLogger @Inject constructor() {
    fun debug(message: String) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, message)
        }
    }

    fun warn(message: String, throwable: Throwable? = null) {
        if (BuildConfig.DEBUG) {
            Log.w(TAG, message, throwable)
        }
    }

    companion object {
        private const val TAG = "TripRealtime"
    }
}
