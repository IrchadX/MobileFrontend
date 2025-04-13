package com.example.mobileuser_frontend.functions

import com.example.mobileuser_frontend.API.ApiResponse
import com.example.mobileuser_frontend.module.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


fun splitFullName(fullName: String): Pair<String, String> { // Now both return non-null
    val parts = fullName.trim().split("\\s+".toRegex())
    return when {
        parts.size >= 2 -> parts.last() to parts.dropLast(1).joinToString(" ")
        parts.size == 1 -> parts[0] to "" // Return empty string instead of null
        else -> "" to "" // Handle empty input
    }
}
//Function to change the user data
fun changeUserData(userId: String, name: String, callback: (String?) -> Unit) {
    val (familyName, firstName) = splitFullName(name)
    val change = RetrofitClient.instance.changeDataUser(userId, firstName, familyName)

    change.enqueue(object : Callback<ApiResponse> {
        override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
            if (response.isSuccessful) {
                val mes= response.body()?.data
                println("Message: $mes")
                callback(mes) // Send phone number to callback
            } else {
                println("Error: ${response.errorBody()?.string()}")
                callback(null)
            }
        }

        override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
            println("Network Error: ${t.message}")
            callback(null)
        }
    })
}
//Function to change the user password
fun changeUserPassword(userId: String, pwd: String, callback: (String?) -> Unit) {
    val change = RetrofitClient.instance.changePasswordUser(userId, pwd)

    change.enqueue(object : Callback<ApiResponse> {
        override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
            if (response.isSuccessful) {
                val mes= response.body()?.data
                println("Message: $mes")
                callback(mes) // Send phone number to callback
            } else {
                println("Error: ${response.errorBody()?.string()}")
                callback(null)
            }
        }

        override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
            println("Network Error: ${t.message}")
            callback(null)
        }
    })
}