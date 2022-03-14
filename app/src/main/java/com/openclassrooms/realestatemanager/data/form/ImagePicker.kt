package com.openclassrooms.realestatemanager.data.form

import androidx.annotation.StringRes
import com.openclassrooms.realestatemanager.R

enum class ImagePicker(@StringRes val labelId: Int) {
    GALLERY(R.string.image_picker_dialog_gallery_item),
    CAMERA(R.string.image_picker_dialog_camera_item),
}