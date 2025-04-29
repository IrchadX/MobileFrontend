package com.example.mobileuser_frontend.repository


import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.mobileuser_frontend.data.model.API.PairResponse
import com.example.mobileuser_frontend.data.model.PairAidantDto
import com.example.mobileuser_frontend.data.model.SignInRequest
import com.example.mobileuser_frontend.data.model.SignInResponse
import com.example.mobileuser_frontend.data.model.SignUpRequest
import com.example.mobileuser_frontend.module.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import org.json.JSONObject
import retrofit2.HttpException

private val Context.authDataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

class AuthRepository(private val context: Context) {
    private val authApi = RetrofitClient.authApi

    companion object {
        val USER_ID = stringPreferencesKey("user_id")
        val AUTH_TOKEN = stringPreferencesKey("auth_token")
        val USER_EMAIL = stringPreferencesKey("user_email")
    }


    suspend fun signIn(email: String, password: String): SignInResponse {
        try {
            val request = SignInRequest(email, password)
            val response = authApi.signIn(request)
            saveAuthInfo(response.user.id.toString(), response.token, email)
            return response
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = try {
                JSONObject(errorBody ?: "").optString("error", "Authentication failed")
            } catch (ex: Exception) {
                "Authentication failed"
            }
            throw Exception(errorMessage)
        } catch (e: Exception) {
            throw Exception(e.message ?: "Unknown error occurred")
        }
    }


    suspend fun signUp(request: SignUpRequest): Int {
        try {
            val response = authApi.signUp(request)
            return response.userId // assuming backend returns `{ message, userId }`
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = try {
                JSONObject(errorBody ?: "").optString("error", "Signup failed")
            } catch (ex: Exception) {
                "Signup failed"
            }
            throw Exception(errorMessage)
        } catch (e: Exception) {
            throw Exception(e.message ?: "Unknown error occurred")
        }
    }



    suspend fun saveAuthInfo(userId: String, token: String, email: String) {
        context.authDataStore.edit { preferences ->
            preferences[USER_ID] = userId
            preferences[AUTH_TOKEN] = token
            preferences[USER_EMAIL] = email
        }
    }

    fun getUserId(): Flow<String?> {
        return context.authDataStore.data.map { preferences ->
            preferences[USER_ID]
        }
    }


    fun getAuthToken(): Flow<String?> {
        return context.authDataStore.data.map { preferences ->
            preferences[AUTH_TOKEN]
        }
    }

    fun getUserEmail(): Flow<String?> {
        return context.authDataStore.data.map { preferences ->
            preferences[USER_EMAIL]
        }
    }

    suspend fun clearAuthInfo() {
        context.authDataStore.edit { it.clear() }
    }


    suspend fun isLoggedIn(): Boolean {
        val token = getAuthToken().firstOrNull()
        return !token.isNullOrEmpty() && validateToken(token)
    }


    suspend fun validateToken(token: String): Boolean {
        return try {
            val response = authApi.validateToken("Bearer $token")
            response.valid
        } catch (e: Exception) {
            false
        }
    }
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
