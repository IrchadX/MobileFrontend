package com.example.mobileuser_frontend.viewmodel

import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobileuser_frontend.repository.PairingRepository
import com.example.mobileuser_frontend.state.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PairingViewModel @RequiresExtension(
    extension = Build.VERSION_CODES.S,
    version = 7
) constructor(
    val authRepository: PairingRepository = PairingRepository()
) : ViewModel() {

    // Use the generic UiState with String as the success data type
    private val _state = MutableStateFlow<UiState<String>>(UiState.Idle)
    val state: StateFlow<UiState<String>> = _state.asStateFlow()

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    fun pair(aidantIdentifier: String, userId: Int) {
        viewModelScope.launch {
            _state.value = UiState.Loading

            try {
                val response = withContext(Dispatchers.IO) {
                    authRepository.pair(aidantIdentifier, userId)
                }
                // Pass the successful response message to the UI
                _state.value = UiState.Success(response.message)
            } catch (e: Exception) {
                _state.value = UiState.Error(e.message ?: "Pairing failed")
            }
        }
    }
}