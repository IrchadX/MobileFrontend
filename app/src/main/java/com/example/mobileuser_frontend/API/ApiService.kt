package com.example.mobileuser_frontend.API


import com.example.mobileuser_frontend.module.ListItems
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

data class ApiResponse(val data: String)

interface ApiService {
    @GET("api/appel/{id}")
    fun getPhoneNumber(@Path("id") userId: String): Call<ApiResponse>
    @GET("api/emergencyappel/")
    fun getEmergencyList(): Call<List<ListItems>>
    @GET("api/getDataProfil/{id}")
    fun getData(@Path("id") userId: String) : Call<ApiResponse>
    @POST("api/changeDataProfil/{id}/{first}/{last}") // Accepts 'id', 'first', and 'last' as parameters
    fun changeDataUser(@Path("id") userId: String, @Path("first") firstName: String, @Path("last") lastName: String) : Call<ApiResponse>
    @POST("api/changePassword/{id}/{pwd}")
    fun changePasswordUser(@Path("id") userId: String, @Path("pwd") pwd: String) : Call<ApiResponse>

}

