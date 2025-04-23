package com.example.mobileuser_frontend.functions

import com.example.mobileuser_frontend.API.ProfilData
import com.example.mobileuser_frontend.module.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun fetchUserInfo(userId: String, callback: (String?) -> Unit) {
    val call = RetrofitClient.instance.getData(userId)

    call.enqueue(object : Callback<ProfilData> {
        override fun onResponse(call: Call<ProfilData>, response: Response<ProfilData>) {
            if (response.isSuccessful) {
                val name= response.body()?.fullName
                callback(name)
            } else {
                println("Error: ${response.errorBody()?.string()}")
                callback(null)
            }
        }

        override fun onFailure(call: Call<ProfilData>, t: Throwable) {
            println("Network Error: ${t.message}")
            callback(null)
        }
    })
}