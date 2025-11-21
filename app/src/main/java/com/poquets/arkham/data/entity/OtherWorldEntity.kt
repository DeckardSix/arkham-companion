package com.poquets.arkham.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "other_worlds")
data class OtherWorldEntity(
    @PrimaryKey val id: String,
    val name: String,
    val expansionId: String,
    val frontImagePath: String,
    val color: String
) 