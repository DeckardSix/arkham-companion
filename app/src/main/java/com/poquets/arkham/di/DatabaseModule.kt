package com.poquets.arkham.di

import android.content.Context
import com.poquets.arkham.data.AppDatabase
import com.poquets.arkham.data.dao.ExpansionDao
import com.poquets.arkham.data.dao.LocationDao
import com.poquets.arkham.data.dao.EncounterDao
import com.poquets.arkham.data.dao.OtherWorldDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }
    
    @Provides
    fun provideExpansionDao(database: AppDatabase): ExpansionDao {
        return database.expansionDao()
    }
    
    @Provides
    fun provideLocationDao(database: AppDatabase): LocationDao {
        return database.locationDao()
    }
    
    @Provides
    fun provideEncounterDao(database: AppDatabase): EncounterDao {
        return database.encounterDao()
    }
    
    @Provides
    fun provideOtherWorldDao(database: AppDatabase): OtherWorldDao {
        return database.otherWorldDao()
    }
} 