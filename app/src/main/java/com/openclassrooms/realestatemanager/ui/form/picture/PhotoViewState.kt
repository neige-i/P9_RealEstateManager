package com.openclassrooms.realestatemanager.ui.form.picture

import android.net.Uri

data class PhotoViewState(
    val uri: Uri?,
    val isUriErrorVisible: Boolean,
    val description: String,
    val descriptionError: String?,
)
