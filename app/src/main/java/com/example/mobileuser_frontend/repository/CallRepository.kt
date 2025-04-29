package com.example.mobileuser_frontend.repository


import com.example.mobileuser_frontend.data.model.ApiResponse
import com.example.mobileuser_frontend.data.model.DeviceData
import com.example.mobileuser_frontend.data.model.PasswordRequest
import com.example.mobileuser_frontend.data.model.ProfilRequest
import com.example.mobileuser_frontend.module.ListItems
import com.example.mobileuser_frontend.module.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CallRepository {

    private val apiService = RetrofitClient.instance

    // Fetch phone number
    suspend fun fetchPhoneNumber(userId: String): ApiResponse? {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getPhoneNumber(userId).execute()
                if (response.isSuccessful) response.body() else null
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    // Fetch emergency list
    suspend fun fetchEmergencyList(): List<ListItems>? {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getEmergencyList().execute()
                if (response.isSuccessful) response.body() else null
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    // Update user profile data
    suspend fun updateUserProfile(request: ProfilRequest): ApiResponse? {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.changeDataUser(request).execute()
                if (response.isSuccessful) response.body() else null
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    // Change user password
    suspend fun changePassword(request: PasswordRequest): ApiResponse? {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.changePasswordUser(request).execute()
                if (response.isSuccessful) response.body() else null
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    // Check user password
    suspend fun checkPasswordUser(request: PasswordRequest): ApiResponse? {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.checkPasswordUser(request).execute()
                if (response.isSuccessful) response.body() else null
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    // Fetch device data
    suspend fun fetchDeviceData(userId: String): DeviceData? {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getDeviceData(userId).execute()
                if (response.isSuccessful) response.body() else null
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}