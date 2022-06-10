package com.openclassrooms.realestatemanager.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.openclassrooms.realestatemanager.data.PointOfInterest

@Entity
data class PoiEntity(
    @PrimaryKey val poiValue: PointOfInterest,
)
