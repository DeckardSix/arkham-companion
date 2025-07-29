package com.pqt.arkham.data.dao

import androidx.room.*
import com.pqt.arkham.data.entity.LocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    
    @Query("SELECT * FROM locations ORDER BY name ASC")
    fun getAllLocations(): Flow<List<LocationEntity>>
    
    @Query("SELECT * FROM locations WHERE expansionId IN (SELECT id FROM expansions WHERE isEnabled = 1) ORDER BY name ASC")
    fun getLocationsForEnabledExpansions(): Flow<List<LocationEntity>>
    
    @Query("SELECT * FROM locations WHERE neighborhoodId = :neighborhoodId AND expansionId IN (SELECT id FROM expansions WHERE isEnabled = 1)")
    fun getLocationsByNeighborhood(neighborhoodId: String): Flow<List<LocationEntity>>
    
    @Query("SELECT * FROM locations WHERE id = :locationId")
    suspend fun getLocationById(locationId: String): LocationEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: LocationEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocations(locations: List<LocationEntity>)
    
    @Update
    suspend fun updateLocation(location: LocationEntity)
    
    @Delete
    suspend fun deleteLocation(location: LocationEntity)
    
    @Query("DELETE FROM locations")
    suspend fun deleteAllLocations()
} 