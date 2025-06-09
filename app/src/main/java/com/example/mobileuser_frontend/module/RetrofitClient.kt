package com.example.mobileuser_frontend.module

import com.example.mobileuser_frontend.data.model.API.ApiService
import com.example.mobileuser_frontend.data.model.API.AuthApi
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "http://172.20.10.3:3000"
    //private const val BASE_URL = "https://f890-41-111-189-175.ngrok-free.app"

    // Logging interceptor (useful for debugging)
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // OkHttp client with logging
    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    // Single Retrofit instance
    private val retrofitInstance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder().setLenient().create()
                )
            )
            .build()
    }

    // APIs
    val authApi: AuthApi by lazy {
        retrofitInstance.create(AuthApi::class.java)
    }

    val instance: ApiService by lazy {
        retrofitInstance.create(ApiService::class.java)
    }
   }