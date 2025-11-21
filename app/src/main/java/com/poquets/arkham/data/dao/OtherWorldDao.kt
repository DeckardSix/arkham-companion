package com.poquets.arkham.data.dao

import androidx.room.*
import com.poquets.arkham.data.entity.OtherWorldEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OtherWorldDao {
    
    @Query("SELECT * FROM other_worlds ORDER BY name ASC")
    fun getAllOtherWorlds(): Flow<List<OtherWorldEntity>>
    
    @Query("SELECT * FROM other_worlds WHERE expansionId IN (SELECT id FROM expansions WHERE isEnabled = 1) ORDER BY name ASC")
    fun getOtherWorldsForEnabledExpansions(): Flow<List<OtherWorldEntity>>
    
    @Query("SELECT * FROM other_worlds WHERE color = :color AND expansionId IN (SELECT id FROM expansions WHERE isEnabled = 1)")
    fun getOtherWorldsByColor(color: String): Flow<List<OtherWorldEntity>>
    
    @Query("SELECT * FROM other_worlds WHERE id = :otherWorldId")
    suspend fun getOtherWorldById(otherWorldId: String): OtherWorldEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOtherWorld(otherWorld: OtherWorldEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOtherWorlds(otherWorlds: List<OtherWorldEntity>)
    
    @Update
    suspend fun updateOtherWorld(otherWorld: OtherWorldEntity)
    
    @Delete
    suspend fun deleteOtherWorld(otherWorld: OtherWorldEntity)
    
    @Query("DELETE FROM other_worlds")
    suspend fun deleteAllOtherWorlds()
} 