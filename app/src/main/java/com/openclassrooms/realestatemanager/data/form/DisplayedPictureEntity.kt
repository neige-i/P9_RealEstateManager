package com.openclassrooms.realestatemanager.data.form

import android.net.Uri

data class DisplayedPictureEntity(
    val uri: Uri,
    val description: String,
    val descriptionError: String?,
    val descriptionCursor: Int,
)
