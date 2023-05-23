package com.rodaClone.driver.connection

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Intercept all apis calls via Retrofit
 * Header Intercept ---------NOT USED as of NOW----------
 */

class HeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        request = request.newBuilder().apply {
            addHeader("Accept", "application/json")
        }.build()

        return chain.proceed(request)
    }
}