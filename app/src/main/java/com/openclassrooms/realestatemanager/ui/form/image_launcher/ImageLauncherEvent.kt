package com.openclassrooms.realestatemanager.ui.form.image_launcher

sealed class ImageLauncherEvent {

    object OpenGallery : ImageLauncherEvent()

    object OpenCamera : ImageLauncherEvent()
}
