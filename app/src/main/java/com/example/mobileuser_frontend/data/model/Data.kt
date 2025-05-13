package com.example.mobileuser_frontend.data.model

import com.google.gson.annotations.SerializedName

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
data class PairAidantDto(
    val user_id: Int,
    val aidant_identifier: String,
)

data class UserLocation(
    val latitude: Double,
    val longitude: Double
)
data class LocationRequest(
    val userId: String,
    val latitude: String,
    val longitude: String
)