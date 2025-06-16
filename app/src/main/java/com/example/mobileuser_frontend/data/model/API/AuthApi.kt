package com.example.mobileuser_frontend.data.model.API

import com.example.mobileuser_frontend.data.model.PairAidantDto
import com.example.mobileuser_frontend.data.model.SignInRequest
import com.example.mobileuser_frontend.data.model.SignInResponse
import com.example.mobileuser_frontend.data.model.SignUpRequest
import com.example.mobileuser_frontend.data.model.SignUpResponse
import com.example.mobileuser_frontend.data.model.TokenValidationResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST


interface AuthApi {
    @POST("auth")
    suspend fun signIn(@Body request: SignInRequest): SignInResponse
    @GET("auth/validate")
    suspend fun validateToken(@Header("Authorization") token: String): TokenValidationResponse
    @POST("auth/signup")
    suspend fun signUp(@Body request: SignUpRequest): SignUpResponse
    @POST("aidant/pair")
    fun pairWithAidant(@Body dto: PairAidantDto): Call<PairResponse>
}