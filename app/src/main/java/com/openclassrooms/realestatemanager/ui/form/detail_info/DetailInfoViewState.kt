package com.openclassrooms.realestatemanager.ui.form.detail_info

import android.net.Uri

data class DetailInfoViewState(
    val description: String,
    val descriptionSelection: Int,
    val photoList: List<PhotoViewState>,
) {

    sealed class PhotoViewState {

        object Add : PhotoViewState()

        data class Picture(
            val uri: Uri,
            val description: String,
        ) : PhotoViewState()
    }
}
