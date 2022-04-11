package com.openclassrooms.realestatemanager.data.room

import androidx.room.Entity

@Entity(primaryKeys = ["realEstateId", "poiValue"])
data class EstatePoiCrossRef(
    val realEstateId: Long,
    val poiValue: String,
)