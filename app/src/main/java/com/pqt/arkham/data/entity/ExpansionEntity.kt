package com.pqt.arkham.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expansions")
data class ExpansionEntity(
    @PrimaryKey val id: String,
    val name: String,
    val checkboxOnPath: String,
    val checkboxOffPath: String,
    val isEnabled: Boolean = false
) 