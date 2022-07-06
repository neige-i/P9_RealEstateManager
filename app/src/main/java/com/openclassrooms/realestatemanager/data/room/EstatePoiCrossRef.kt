package com.openclassrooms.realestatemanager.data.room

import androidx.room.Entity
import com.openclassrooms.realestatemanager.data.PointOfInterest

@Entity(primaryKeys = ["estateId", "poiValue"])
data class EstatePoiCrossRef(
    val estateId: Long,
    val poiValue: PointOfInterest,
)