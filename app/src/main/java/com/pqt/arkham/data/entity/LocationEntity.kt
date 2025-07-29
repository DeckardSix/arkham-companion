package com.pqt.arkham.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "locations")
data class LocationEntity(
    @PrimaryKey val id: String,
    val name: String,
    val expansionId: String,
    val frontImagePath: String,
    val neighborhoodId: String
) 