package com.example.mobileuser_frontend.viewmodel



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobileuser_frontend.data.model.ApiResponse
import com.example.mobileuser_frontend.data.model.DeviceData
import com.example.mobileuser_frontend.data.model.PasswordRequest
import com.example.mobileuser_frontend.data.model.ProfilRequest
import com.example.mobileuser_frontend.module.ListItems
import com.example.mobileuser_frontend.repository.CallRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CallViewModel(private val repository: CallRepository) : ViewModel() {

    private val _phoneNumberState = MutableStateFlow<ApiResponse?>(null)
    val phoneNumberState: StateFlow<ApiResponse?> get() = _phoneNumberState

    private val _emergencyListState = MutableStateFlow<List<ListItems>?>(null)
    val emergencyListState: StateFlow<List<ListItems>?> get() = _emergencyListState

    private val _profileUpdateState = MutableStateFlow<ApiResponse?>(null)
    val profileUpdateState: StateFlow<ApiResponse?> get() = _profileUpdateState

    private val _passwordChangeState = MutableStateFlow<ApiResponse?>(null)
    val passwordChangeState: StateFlow<ApiResponse?> get() = _passwordChangeState

    private val _deviceDataState = MutableStateFlow<DeviceData?>(null)
    val deviceDataState: StateFlow<DeviceData?> get() = _deviceDataState

    // Fetch phone number
    fun fetchPhoneNumber(userId: String) {
        viewModelScope.launch {
            _phoneNumberState.value = repository.fetchPhoneNumber(userId)
        }
    }

    // Fetch emergency list
    fun fetchEmergencyList() {
        viewModelScope.launch {
            _emergencyListState.value = repository.fetchEmergencyList()
        }
    }

    // Update user profile
    fun updateUserProfile(request: ProfilRequest) {
        viewModelScope.launch {
            _profileUpdateState.value = repository.updateUserProfile(request)
        }
    }

    // Change user password
    fun changePassword(request: PasswordRequest) {
        viewModelScope.launch {
            _passwordChangeState.value = repository.changePassword(request)
        }
    }

    // Fetch device data
    fun fetchDeviceData(userId: String) {
        viewModelScope.launch {
            _deviceDataState.value = repository.fetchDeviceData(userId)
        }
    }
}