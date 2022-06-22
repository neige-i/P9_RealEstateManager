package com.openclassrooms.realestatemanager.ui.form.picture

import android.net.Uri

data class PictureViewState(
    val uri: Uri,
    val description: String,
    val descriptionError: String?,
)
