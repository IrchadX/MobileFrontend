package com.example.mobileuser_frontend.API


import com.example.mobileuser_frontend.module.ListItems
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

data class ApiResponse(val data: String)
data class ProfilData(val fullName: String)
data class ProfilRequest(val id : String , val firstName: String, val lastName: String)
data class PasswordRequest(val id : String , val pwd: String)
data class DeviceData(val type: String="", val state :  String="Deconnected", val mac_address: String = "")

interface ApiService {
    @GET("api/appel/{id}")
    fun getPhoneNumber(@Path("id") userId: String): Call<ApiResponse>
    @GET("api/emergencyappel/")
    fun getEmergencyList(): Call<List<ListItems>>
    @GET("api/getDataProfil/{id}")
    fun getData(@Path("id") userId: String) : Call<ProfilData>
    @POST("api/changeDataProfil") // Accepts 'id', 'first', and 'last' as parameters
    fun changeDataUser(@Body request: ProfilRequest) : Call<ApiResponse>
    @POST("api/changePassword")
    fun changePasswordUser(@Body passwordRequest: PasswordRequest) : Call<ApiResponse>
    @POST("api/checkPassword")
    fun checkPasswordUser(@Body passwordRequest: PasswordRequest ) : Call<ApiResponse>
    @GET("api/getDeviceInfo/{id}")
    fun getDeviceData(@Path("id") userId: String) : Call<DeviceData>

}

