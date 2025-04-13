package com.example.mobileuser_frontend.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.mobileuser_frontend.functions.fetchEmergencyList
import com.example.mobileuser_frontend.module.ListItems

class EmergencyViewModel : ViewModel() {
    private val _dropdownItems = mutableStateOf<List<ListItems>>(emptyList())
    val dropdownItems: State<List<ListItems>> get() = _dropdownItems

    init {
        fetchEmergencyContacts()
    }

    private fun fetchEmergencyContacts() {
        fetchEmergencyList { list ->
            list?.let {
                _dropdownItems.value = it.map { contact ->
                    ListItems(label = contact.label, number = contact.number)
                }
            }
        }
    }
}