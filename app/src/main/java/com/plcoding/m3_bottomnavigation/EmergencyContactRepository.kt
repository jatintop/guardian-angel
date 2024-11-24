package com.plcoding.m3_bottomnavigation

import kotlinx.coroutines.flow.Flow


class EmergencyContactRepository(private val contactDao: EmergencyContactDao) {
    fun getAllContacts(): Flow<List<EmergencyContact>> = contactDao.getAllContacts()

    suspend fun addContact(contact: EmergencyContact) {
        contactDao.insertContact(contact)
    }

    suspend fun deleteContact(contact: EmergencyContact) {
        contactDao.deleteContact(contact)
    }
}