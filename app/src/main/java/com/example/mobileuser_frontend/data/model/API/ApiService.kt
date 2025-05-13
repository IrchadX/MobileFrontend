package com.example.mobileuser_frontend.data.model.API


import com.example.mobileuser_frontend.data.model.ApiResponse
import com.example.mobileuser_frontend.data.model.DeviceData
import com.example.mobileuser_frontend.data.model.LocationRequest
import com.example.mobileuser_frontend.data.model.PasswordRequest
import com.example.mobileuser_frontend.data.model.ProfilData
import com.example.mobileuser_frontend.data.model.ProfilRequest
import com.example.mobileuser_frontend.module.ListItems
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

data class PairResponse (
    val message: String , val userId: Number
)

interface ApiService {
    @GET("api/appel/{id}")
    fun getPhoneNumber(@Path("id") userId: String): Call<ApiResponse>
    @GET("api/emergencyappel/")
    fun getEmergencyList(): Call<List<ListItems>>
    @GET("api/getDataProfil/{id}")
    fun getData(@Path("id") userId: String) : Call<ProfilData>
    @POST("api/changeDataProfil")
    fun changeDataUser(@Body request: ProfilRequest) : Call<ApiResponse>
    @POST("api/changePassword")
    fun changePasswordUser(@Body passwordRequest: PasswordRequest) : Call<ApiResponse>
    @POST("api/checkPassword")
    fun checkPasswordUser(@Body passwordRequest: PasswordRequest ) : Call<ApiResponse>
    @GET("api/getDeviceInfo/{id}")
    fun getDeviceData(@Path("id") userId: String) : Call<DeviceData>

    //Localisation
    @POST("location/update")
    suspend fun updateLocation(@Body body: LocationRequest): ApiResponse

    @POST("location/share")
    suspend fun shareLocation(@Body body: LocationRequest): ApiResponse


}

