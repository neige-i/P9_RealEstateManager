package com.openclassrooms.realestatemanager.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PoiEntity(
    @PrimaryKey(autoGenerate = true) val poiId: Long = 0,
    val poiValue: String,
)
