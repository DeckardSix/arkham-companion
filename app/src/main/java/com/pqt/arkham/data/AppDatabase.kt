package com.pqt.arkham.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.pqt.arkham.data.dao.ExpansionDao
import com.pqt.arkham.data.dao.LocationDao
import com.pqt.arkham.data.dao.EncounterDao
import com.pqt.arkham.data.dao.OtherWorldDao
import com.pqt.arkham.data.entity.ExpansionEntity
import com.pqt.arkham.data.entity.LocationEntity
import com.pqt.arkham.data.entity.EncounterEntity
import com.pqt.arkham.data.entity.OtherWorldEntity
import timber.log.Timber

@Database(
    entities = [
        ExpansionEntity::class,
        LocationEntity::class,
        EncounterEntity::class,
        OtherWorldEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun expansionDao(): ExpansionDao
    abstract fun locationDao(): LocationDao
    abstract fun encounterDao(): EncounterDao
    abstract fun otherWorldDao(): OtherWorldDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "arkham_companion_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 