package com.poquets.arkham.data.dao

import androidx.room.*
import com.poquets.arkham.data.entity.ExpansionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpansionDao {
    
    @Query("SELECT * FROM expansions ORDER BY name ASC")
    fun getAllExpansions(): Flow<List<ExpansionEntity>>
    
    @Query("SELECT * FROM expansions WHERE isEnabled = 1 ORDER BY name ASC")
    fun getEnabledExpansions(): Flow<List<ExpansionEntity>>
    
    @Query("SELECT * FROM expansions WHERE id = :expansionId")
    suspend fun getExpansionById(expansionId: String): ExpansionEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpansion(expansion: ExpansionEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpansions(expansions: List<ExpansionEntity>)
    
    @Update
    suspend fun updateExpansion(expansion: ExpansionEntity)
    
    @Query("UPDATE expansions SET isEnabled = :isEnabled WHERE id = :expansionId")
    suspend fun updateExpansionEnabled(expansionId: String, isEnabled: Boolean)
    
    @Delete
    suspend fun deleteExpansion(expansion: ExpansionEntity)
    
    @Query("DELETE FROM expansions")
    suspend fun deleteAllExpansions()
} 