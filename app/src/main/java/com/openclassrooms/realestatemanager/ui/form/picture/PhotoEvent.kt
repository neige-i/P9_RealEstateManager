package com.openclassrooms.realestatemanager.ui.form.picture

import com.openclassrooms.realestatemanager.ui.util.LocalText

sealed class PhotoEvent {

    data class ShowPickerDialog(val items: List<LocalText>) : PhotoEvent()
    object OpenGallery : PhotoEvent()
    object OpenCamera : PhotoEvent()
    object Exit : PhotoEvent()
}
