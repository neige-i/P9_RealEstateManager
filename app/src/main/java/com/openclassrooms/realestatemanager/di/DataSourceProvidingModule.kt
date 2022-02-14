package com.openclassrooms.realestatemanager.di

import android.content.Context
import androidx.room.Room
import com.openclassrooms.realestatemanager.data.room.AppDatabase
import com.openclassrooms.realestatemanager.data.room.RealEstateDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceProvidingModule {

    @Singleton
    @Provides
    fun provideAddDatabase(@ApplicationContext applicationContext: Context): AppDatabase {
        return Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "app_database.db"
        ).build()
    }

    @Singleton
    @Provides
    fun provideRealEstateDao(appDatabase: AppDatabase): RealEstateDao = appDatabase.realEstateDao()
}