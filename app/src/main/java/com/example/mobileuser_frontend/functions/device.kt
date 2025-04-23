package com.example.mobileuser_frontend.functions

import com.example.mobileuser_frontend.API.DeviceData
import com.example.mobileuser_frontend.module.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

suspend fun fetchDeviceInfo(userId: String, callback: (DeviceData?) -> Unit) {
    val call = RetrofitClient.instance.getDeviceData(userId)

    call.enqueue(object : Callback<DeviceData> {
        override fun onResponse(call: Call<DeviceData>, response: Response<DeviceData>) {
            if (response.isSuccessful) {
                val data= response.body()
                callback(data)
            } else {
                println("Error: ${response.errorBody()?.string()}")
                callback(null)
            }
        }

        override fun onFailure(call: Call<DeviceData>, t: Throwable) {
            println("Network Error: ${t.message}")
            callback(null)
        }
    })
}