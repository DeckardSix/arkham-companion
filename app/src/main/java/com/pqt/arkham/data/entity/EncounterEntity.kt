package com.pqt.arkham.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "encounters")
data class EncounterEntity(
    @PrimaryKey val id: String,
    val locationId: String,
    val encounterText: String,
    val expansionId: String,
    val isPlayed: Boolean = false,
    val playedAt: Long? = null
) 