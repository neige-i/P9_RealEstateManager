package com.openclassrooms.realestatemanager.data.current_photo

import android.net.Uri

data class CurrentPhotoEntity(
    val uri: Uri?,
    val isUriErrorVisible: Boolean,
    val description: String,
    val descriptionError: String?,
)
