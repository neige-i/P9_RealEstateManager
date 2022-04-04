package com.openclassrooms.realestatemanager.data.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        EstateEntity::class,
        PhotoEntity::class,
        PoiEntity::class,
        EstatePoiCrossRef::class,
        AgentEntity::class,
        EstateAgentCrossRef::class,
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun roomDao(): RoomDao
}