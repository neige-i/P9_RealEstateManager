package com.openclassrooms.realestatemanager.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.openclassrooms.realestatemanager.data.real_estate.RealEstateEntity

@Database(entities = [RealEstateEntity::class], version = 1, exportSchema = false)
@TypeConverters(RoomConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun realEstateDao(): RealEstateDao
}