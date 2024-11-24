package com.plcoding.m3_bottomnavigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class EmergencyContactsViewModel(private val repository: EmergencyContactRepository) : ViewModel() {
    private val _contacts = MutableStateFlow<List<EmergencyContact>>(emptyList())
    val contacts: StateFlow<List<EmergencyContact>> = _contacts

    init {
        viewModelScope.launch {
            repository.getAllContacts().collect {
                _contacts.value = it
            }
        }
    }

    fun addContact(contact: EmergencyContact) {
        viewModelScope.launch {
            repository.addContact(contact)
        }
    }

    fun deleteContact(contact: EmergencyContact) {
        viewModelScope.launch {
            repository.deleteContact(contact)
        }
    }

    companion object {
        fun provideFactory(repository: EmergencyContactRepository): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return EmergencyContactsViewModel(repository) as T
            }
        }
    }
}