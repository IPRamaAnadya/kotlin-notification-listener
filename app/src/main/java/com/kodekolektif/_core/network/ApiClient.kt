package com.kodekolektif._core.network

import android.content.SharedPreferences
import android.util.Log
import com.kodekolektif._core.utils.Constant
import com.kodekolektif.notiflistener.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient : KoinComponent {

    private val sharedPreferences: SharedPreferences by inject()

    private fun getBaseUrl(): String {
        val baseUrl = if(sharedPreferences.getString(Constant.baseUrlKey, Constant.baseUrl) == "" || sharedPreferences.getString(Constant.baseUrlKey, Constant.baseUrl) == null) {
            Constant.baseUrl
        } else {
            sharedPreferences.getString(Constant.baseUrlKey, Constant.baseUrl)
        }
        Log.e("ApiClient", "Base Url: $baseUrl")
        return baseUrl + Constant.apiVersion
    }

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
                request = request.newBuilder().build()
                chain.proceed(request)
            }
        }.build()

        val baseUrl = getBaseUrl()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
