package com.example.mobileuser_frontend.repository


import com.example.mobileuser_frontend.module.ListItems
import com.example.mobileuser_frontend.module.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CallRepository {

    private val apiService = RetrofitClient.instance

    // Convert callbacks to suspend functions using coroutines
    suspend fun getPhoneNumber(userId: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getPhoneNumber(userId).execute()
                if (response.isSuccessful) {
                    val phone = response.body()?.data
                    if (phone != null) {
                        Result.success(phone)
                    } else {
                        Result.failure(Exception("Phone number not found"))
                    }
                } else {
                    Result.failure(Exception("Error: ${response.errorBody()?.string()}"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Network Error: ${e.message}"))
            }
        }
    }

    suspend fun getEmergencyList(): Result<List<ListItems>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getEmergencyList().execute()
                if (response.isSuccessful) {
                    val emergencyList = response.body()
                    if (emergencyList != null) {
                        Result.success(emergencyList)
                    } else {
                        Result.failure(Exception("Emergency list is empty"))
                    }
                } else {
                    Result.failure(Exception("Error: ${response.errorBody()?.string()}"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Network Error: ${e.message}"))
            }
        }
    }
}
