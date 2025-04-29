package com.example.mobileuser_frontend.module

import com.example.mobileuser_frontend.API.ApiService
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://3294-154-121-30-70.ngrok-free.app"


    private val retrofitInstance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(
                GsonBuilder().setLenient().create()
            ))
            .build()
    }

    val instance: ApiService by lazy {
        retrofitInstance.create(ApiService::class.java)
    }
}