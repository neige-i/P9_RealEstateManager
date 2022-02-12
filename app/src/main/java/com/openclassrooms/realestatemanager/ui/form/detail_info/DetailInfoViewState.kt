package com.openclassrooms.realestatemanager.ui.form.detail_info

import android.net.Uri

data class DetailInfoViewState(
    val description: String,
    val photoList: List<PhotoViewState>,
) {

    sealed class PhotoViewState {

        data class Add(
            val pictureUri: Uri?,
        ) : PhotoViewState()
    }
}
