package com.example.mobileuser_frontend.state

import com.example.mobileuser_frontend.module.ListItems


sealed class EmergencyUiState {
        object Idle : EmergencyUiState()
        object Loading : EmergencyUiState()
        data class PhoneNumberLoaded(val phoneNumber: String) : EmergencyUiState()
        data class EmergencyListLoaded(val list: List<ListItems>) : EmergencyUiState()
        data class Error(val message: String) : EmergencyUiState()
}
