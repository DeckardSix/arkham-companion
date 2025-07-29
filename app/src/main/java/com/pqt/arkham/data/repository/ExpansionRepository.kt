package com.pqt.arkham.data.repository

import com.pqt.arkham.data.dao.ExpansionDao
import com.pqt.arkham.data.entity.ExpansionEntity
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpansionRepository @Inject constructor(
    private val expansionDao: ExpansionDao
) {
    
    fun getAllExpansions(): Flow<List<ExpansionEntity>> {
        Timber.d("Getting all expansions")
        return expansionDao.getAllExpansions()
    }
    
    fun getEnabledExpansions(): Flow<List<ExpansionEntity>> {
        Timber.d("Getting enabled expansions")
        return expansionDao.getEnabledExpansions()
    }
    
    suspend fun getExpansionById(expansionId: String): ExpansionEntity? {
        Timber.d("Getting expansion by id: $expansionId")
        return expansionDao.getExpansionById(expansionId)
    }
    
    suspend fun insertExpansion(expansion: ExpansionEntity) {
        Timber.d("Inserting expansion: ${expansion.name}")
        expansionDao.insertExpansion(expansion)
    }
    
    suspend fun insertExpansions(expansions: List<ExpansionEntity>) {
        Timber.d("Inserting ${expansions.size} expansions")
        expansionDao.insertExpansions(expansions)
    }
    
    suspend fun updateExpansion(expansion: ExpansionEntity) {
        Timber.d("Updating expansion: ${expansion.name}")
        expansionDao.updateExpansion(expansion)
    }
    
    suspend fun updateExpansionEnabled(expansionId: String, isEnabled: Boolean) {
        Timber.d("Updating expansion enabled: $expansionId -> $isEnabled")
        expansionDao.updateExpansionEnabled(expansionId, isEnabled)
    }
    
    suspend fun deleteExpansion(expansion: ExpansionEntity) {
        Timber.d("Deleting expansion: ${expansion.name}")
        expansionDao.deleteExpansion(expansion)
    }
    
    suspend fun deleteAllExpansions() {
        Timber.d("Deleting all expansions")
        expansionDao.deleteAllExpansions()
    }
} 