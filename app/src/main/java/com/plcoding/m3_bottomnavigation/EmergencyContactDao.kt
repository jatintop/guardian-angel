package com.plcoding.m3_bottomnavigation


import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.plcoding.m3_bottomnavigation.EmergencyContact

@Dao
interface EmergencyContactDao {
    @Query("SELECT * FROM emergency_contacts")
    fun getAllContacts(): Flow<List<EmergencyContact>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: EmergencyContact)

    @Delete
    suspend fun deleteContact(contact: EmergencyContact)
}