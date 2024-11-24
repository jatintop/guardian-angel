package com.plcoding.m3_bottomnavigation

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "emergency_contacts")
data class EmergencyContact(
    @PrimaryKey val id: String,
    val name: String,
    val phoneNumber: String
)
