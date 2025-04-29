package com.example.mobileuser_frontend.repository

import com.example.mobileuser_frontend.data.model.API.PairResponse
import com.example.mobileuser_frontend.data.model.PairAidantDto
import com.example.mobileuser_frontend.module.RetrofitClient
import org.json.JSONObject
import retrofit2.HttpException

class PairingRepository {

    private val authApi = RetrofitClient.authApi
    suspend fun pair( aidant_identifier: String, user_id: Int): PairResponse {
        try {
            val dto = PairAidantDto(user_id, aidant_identifier)
            val response = authApi.pairWithAidant(dto)
            return response.execute().body() ?: throw Exception("Null response body")
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = try {
                JSONObject(errorBody ?: "").optString("error", "Pairing failed")
            } catch (ex: Exception) {
                "Authentication failed"
            }
            throw Exception(errorMessage)
        } catch (e: Exception) {
            throw Exception(e.message ?: "Unknown error occurred")
        }
    }
}