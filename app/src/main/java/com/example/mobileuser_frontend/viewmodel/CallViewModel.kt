package com.example.mobileuser_frontend.viewmodel



import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobileuser_frontend.repository.CallRepository
import com.example.mobileuser_frontend.state.EmergencyUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CallViewModel(
    private val repository: CallRepository = CallRepository()
) : ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow<EmergencyUiState>(EmergencyUiState.Idle)
    val uiState: StateFlow<EmergencyUiState> = _uiState.asStateFlow()

    private val _uiEmergencyState = MutableStateFlow<EmergencyUiState>(EmergencyUiState.Idle)
    val uiEmergencyState: StateFlow<EmergencyUiState> = _uiEmergencyState.asStateFlow()

    // Function to fetch phone number
    fun fetchPhoneNumber(userId: String) {
        viewModelScope.launch {
            _uiState.value = EmergencyUiState.Loading
            repository.getPhoneNumber(userId).fold(
                onSuccess = { number ->
                    _uiState.value = EmergencyUiState.PhoneNumberLoaded(number)
                },
                onFailure = { exception ->
                    _uiState.value = EmergencyUiState.Error(exception.message ?: "Unknown error")
                }
            )
        }
    }

    // Function to fetch emergency list
    fun fetchEmergencyList() {
        viewModelScope.launch {
            _uiEmergencyState.value = EmergencyUiState.Loading
            repository.getEmergencyList().fold(
                onSuccess = { list ->
                    _uiEmergencyState.value = EmergencyUiState.EmergencyListLoaded(list)
                },
                onFailure = { exception ->
                    _uiEmergencyState.value = EmergencyUiState.Error(exception.message ?: "Unknown error")
                }
            )
        }
    }

    fun makePhoneCall(
        context: Context,
        phoneNumber: String,
        requestPermissionLauncher: ActivityResultLauncher<String>
    ) {
        val callIntent = Intent(Intent.ACTION_CALL).apply {
            data = Uri.parse("tel:$phoneNumber")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        // Check if permission is granted
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            context.startActivity(callIntent)
        } else {
            // Request the CALL_PHONE permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, Manifest.permission.CALL_PHONE)) {
                Toast.makeText(context, "Phone call permission is required to make a call.", Toast.LENGTH_LONG).show()
            }
            requestPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
        }
    }
}