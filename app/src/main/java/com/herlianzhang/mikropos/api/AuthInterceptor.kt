package com.herlianzhang.mikropos.api

import com.herlianzhang.mikropos.utils.UserPreferences
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

class AuthInterceptor @Inject constructor(private val userPref: UserPreferences) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .header("Accept", "application/json")
            .also {
                val accessToken = userPref.accessToken ?: return@also
                it.addHeader(
                    "Authorization",
                    "Bearer $accessToken"
                )
            }.build()
        val response = chain.proceed(request)

        if (response.code == 401) {
            // todo auto logout
        }

        return response
    }
}