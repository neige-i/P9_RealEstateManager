package com.openclassrooms.realestatemanager.ui.form

sealed class FormEvent {

    object ExitActivity : FormEvent()

    object ExitFragment : FormEvent()

    data class ShowDialog(
        val title: String,
        val message: String,
        val positiveButtonText: String,
        val negativeButtonText: String,
    ) : FormEvent()

    object ShowPicture : FormEvent()

    object OpenGallery : FormEvent()

    object OpenCamera : FormEvent()
}
