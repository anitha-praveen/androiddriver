package com.rodaClone.driver.di

import com.google.gson.Gson
import com.rodaClone.driver.connection.ConnectionHelper
import com.rodaClone.driver.connection.HeaderInterceptor
import com.rodaClone.driver.ut.Config
import com.rodaClone.driver.ut.MapsHelper
import dagger.Module
import dagger.Provides
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.engineio.client.transports.WebSocket
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URISyntaxException
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
object NetworkModule {
    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): ConnectionHelper {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .baseUrl(Config.BASEURL)
            .build()
        return retrofit.create(ConnectionHelper::class.java)
    }

    @Singleton
    @Provides
    fun provideGson() = Gson()

    @Singleton
    @Provides
    fun provideOkHttpClient() = OkHttpClient().newBuilder().apply {
        addInterceptor(HttpLoggingInterceptor().apply {
//             if(BuildConfig.DEBUG)
            level = HttpLoggingInterceptor.Level.BODY
        })
        addInterceptor(HeaderInterceptor())
        connectTimeout(0, TimeUnit.SECONDS)
        readTimeout(0, TimeUnit.SECONDS)
        writeTimeout(0, TimeUnit.SECONDS)
    }.build()

    @Provides
    @Singleton
    fun provideSocket(): Socket? {
        var socket: Socket? = null
        val opts = IO.Options()
        opts.forceNew = true
        opts.reconnection = true
        opts.transports = arrayOf(WebSocket.NAME)
        try {
            socket = IO.socket(Config.SOCKET_URL, opts)
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
        return socket
    }

    @Singleton
    @Provides
    fun provideRetrofitMaps(okHttpClient: OkHttpClient, gson: Gson): MapsHelper {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .baseUrl(Config.GooglBaseURL)
            .build()
        return retrofit.create(MapsHelper::class.java)
    }
}