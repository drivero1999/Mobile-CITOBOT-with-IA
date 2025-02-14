package com.yulagarces.citobot.data.services

import android.content.Context
import com.yulagarces.citobot.utils.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor to add auth token to requests
 */
class AuthInterceptor(context: Context) : Interceptor {
    private val sessionManager = SessionManager(context)

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        // If token has been saved, add it to the request
        sessionManager.fetchAuthToken()?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
            requestBuilder.addHeader("content-type", "application/json")
        }

        return chain.proceed(requestBuilder.build())
    }
}