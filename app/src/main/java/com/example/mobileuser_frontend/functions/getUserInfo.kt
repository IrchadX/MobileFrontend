package com.example.mobileuser_frontend.functions

import com.example.mobileuser_frontend.API.ApiResponse
import com.example.mobileuser_frontend.module.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun fetchUserInfo(userId: String, callback: (String?) -> Unit) {
    val call = RetrofitClient.instance.getData(userId)

    call.enqueue(object : Callback<ApiResponse> {
        override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
            if (response.isSuccessful) {
                val name= response.body()?.data
                println("Phone Number: $name")
                callback(name) // Send phone number to callback
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