package com.openclassrooms.realestatemanager.data.room

import androidx.room.Entity

@Entity(primaryKeys = ["realEstateId", "username"])
data class EstateAgentCrossRef(
    val realEstateId: Long,
    val username: String,
)