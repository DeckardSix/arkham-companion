package com.poquets.arkham.data.dao

import androidx.room.*
import com.poquets.arkham.data.entity.EncounterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EncounterDao {
    
    @Query("SELECT * FROM encounters WHERE locationId = :locationId ORDER BY playedAt DESC")
    fun getEncountersByLocation(locationId: String): Flow<List<EncounterEntity>>
    
    @Query("SELECT * FROM encounters WHERE isPlayed = 1 ORDER BY playedAt DESC")
    fun getPlayedEncounters(): Flow<List<EncounterEntity>>
    
    @Query("SELECT * FROM encounters WHERE locationId = :locationId AND isPlayed = 1 ORDER BY playedAt DESC")
    fun getPlayedEncountersByLocation(locationId: String): Flow<List<EncounterEntity>>
    
    @Query("SELECT * FROM encounters WHERE id = :encounterId")
    suspend fun getEncounterById(encounterId: String): EncounterEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEncounter(encounter: EncounterEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEncounters(encounters: List<EncounterEntity>)
    
    @Update
    suspend fun updateEncounter(encounter: EncounterEntity)
    
    @Query("UPDATE encounters SET isPlayed = :isPlayed, playedAt = :playedAt WHERE id = :encounterId")
    suspend fun updateEncounterPlayed(encounterId: String, isPlayed: Boolean, playedAt: Long?)
    
    @Delete
    suspend fun deleteEncounter(encounter: EncounterEntity)
    
    @Query("DELETE FROM encounters")
    suspend fun deleteAllEncounters()
} 