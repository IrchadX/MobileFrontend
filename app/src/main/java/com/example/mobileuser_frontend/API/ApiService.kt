package com.example.mobileuser_frontend.API


import com.example.mobileuser_frontend.module.ListItems
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

data class ApiResponse(val data: String)
data class ProfilData(val fullName: String)
data class ProfilRequest(val id : String , val firstName: String, val lastName: String)
data class PasswordRequest(val id : String , val pwd: String)
data class DeviceData(val type: String="", val state :  String="Deconnected", val mac_address: String = "")
data class SignInRequest(
    val email: String,
    val password: String
)

data class SignInResponse(
    val success: Boolean,
    val message: String? = null,
    val token: String,
    val user: User
)
data class SignUpRequest(
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("family_name")
    val familyName: String,
    val email: String,
    val password: String,
    val confirmPassword: String
)
data class SignUpResponse(
    val message: String,
    val userId: Int
)
data class TokenValidationResponse(
    val valid: Boolean,
    val userId: String? = null
)
data class User(
    val id: Int,

    @SerializedName("family_name")
    val familyName: String?,

    @SerializedName("first_name")
    val firstName: String?,

    @SerializedName("phone_number")
    val phoneNumber: String?,

    val password: String?,

    @SerializedName("userTypeId")
    val userTypeId: Int?,

    val email: String?,
    val sex: String?,
    val street: String?,
    val city: String?,
    @SerializedName("Identifier")
    val identifier: String?,

    @SerializedName("birthDate")
    val birthDate: String?,

    @SerializedName("userType")
    val userType: String?
)
data class UsersResponse(
    val message: String,
    val data: List<HelperUser>
)

data class HelperUser(
    val id: Int,
    @SerializedName("helper_id")
    val helperId: Int,
    @SerializedName("user_id")
    val userId: Int,
    val state: String,
    @SerializedName("user_helper_user_user_idTouser")
    val userHelperUserUserIdTouser: User
)
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
    @POST("/auth")
    suspend fun signIn(@Body request: SignInRequest): SignInResponse
    @GET("/auth/validate")
    suspend fun validateToken(@Header("Authorization") token: String): TokenValidationResponse
    @POST("/aidant/signup")
    suspend fun signUp(@Body request: SignUpRequest): SignUpResponse

}

