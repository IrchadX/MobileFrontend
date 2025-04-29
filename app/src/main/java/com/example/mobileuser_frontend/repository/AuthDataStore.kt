package com.example.mobileuser_frontend.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map


private val Context.authDataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

class AuthDataStore(private val context: Context) {

    companion object {
        val USER_ID = stringPreferencesKey("user_id")
        val AUTH_TOKEN = stringPreferencesKey("auth_token")
    }

    suspend fun saveAuthInfo(userId: String, token: String) {
        context.authDataStore.edit { preferences ->
            preferences[USER_ID] = userId
            preferences[AUTH_TOKEN] = token
        }
    }
    fun getUserId(): Flow<String?> {
        return context.authDataStore.data
            .map { preferences -> preferences[USER_ID] }
    }
    suspend fun isUserLoggedIn(): Boolean {
        val userId = getUserId().firstOrNull()
        return !userId.isNullOrBlank()
    }


}