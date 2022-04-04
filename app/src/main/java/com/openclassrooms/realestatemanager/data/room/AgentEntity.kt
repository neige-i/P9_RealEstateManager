package com.openclassrooms.realestatemanager.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AgentEntity(
    @PrimaryKey(autoGenerate = true) val agentId: Long = 0,
    val username: String,
)