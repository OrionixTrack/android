package com.nestorian87.orionix_track.data.remote.interceptor

import com.nestorian87.orionix_track.data.local.SessionLocalDataSource
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val sessionLocalDataSource: SessionLocalDataSource
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { sessionLocalDataSource.session.firstOrNull()?.accessToken }
        val request = chain.request().newBuilder().apply {
            if (token != null) {
                addHeader("Authorization", "Bearer $token")
            }
        }.build()
        return chain.proceed(request)
    }
}
