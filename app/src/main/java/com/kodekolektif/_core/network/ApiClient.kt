package com.kodekolektif._core.network

import com.kodekolektif._core.utils.Constant
import com.kodekolektif.notiflistener.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    fun init(): Retrofit {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        val okHttpClient = OkHttpClient.Builder().apply {
            readTimeout(Constant.requestTimeout, TimeUnit.SECONDS)
            writeTimeout(Constant.requestTimeout, TimeUnit.SECONDS)
            connectTimeout(Constant.requestTimeout, TimeUnit.SECONDS)
            addInterceptor(loggingInterceptor)
            addInterceptor { chain ->
                var request = chain.request()
                request = request.newBuilder()
                    .build()
                val response = chain.proceed(request)
                response
            }
        }
            .build()

        return Retrofit.Builder()
            .baseUrl(Constant.apiUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}