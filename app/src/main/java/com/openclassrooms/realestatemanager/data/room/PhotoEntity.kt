package com.openclassrooms.realestatemanager.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PhotoEntity(
    @PrimaryKey(autoGenerate = true) val photoId: Long = 0,
    val estateId: Long,
    val uri: String,
    val description: String,
)
