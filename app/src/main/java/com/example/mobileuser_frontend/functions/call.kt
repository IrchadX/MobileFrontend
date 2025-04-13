package com.example.mobileuser_frontend.functions

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.mobileuser_frontend.API.ApiResponse
import com.example.mobileuser_frontend.module.ListItems
import com.example.mobileuser_frontend.module.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


fun fetchPhoneNumber(userId: String, callback: (String?) -> Unit) {
    val call = RetrofitClient.instance.getPhoneNumber(userId)

    call.enqueue(object : Callback<ApiResponse> {
        override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
            if (response.isSuccessful) {
                val phone = response.body()?.data
                println("Phone Number: $phone")
                callback(phone) // Send phone number to callback
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

fun fetchEmergencyList(callback: (List<ListItems>?) -> Unit) {
    val call = RetrofitClient.instance.getEmergencyList()

    call.enqueue(object : Callback<List<ListItems>> {
        override fun onResponse(call: Call<List<ListItems>>, response: Response<List<ListItems>>) {
            if (response.isSuccessful) {
                val emergencyList = response.body()
                callback(emergencyList) // Pass the list to callback
            } else {
                println("Error: ${response.errorBody()?.string()}")
                callback(null)
            }
        }

        override fun onFailure(call: Call<List<ListItems>>, t: Throwable) {
            println("Network Error: ${t.message}")
            callback(null)
        }
    })
}

fun makePhoneCall(context: Context, phoneNumber: String) {



            val callIntent = Intent(Intent.ACTION_CALL).apply {
                data = Uri.parse("tel:$phoneNumber")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            // Check if permission is granted
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                context.startActivity(callIntent)
            } else {
                Toast.makeText(context, "CALL_PHONE permission required", Toast.LENGTH_SHORT).show()
            }


}