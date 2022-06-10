package com.openclassrooms.realestatemanager.data.room

import androidx.room.Entity

@Entity(primaryKeys = ["estateId", "username"])
data class EstateAgentCrossRef(
    val estateId: Long,
    val username: String,
)