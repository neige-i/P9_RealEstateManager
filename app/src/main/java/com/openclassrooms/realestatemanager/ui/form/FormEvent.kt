package com.openclassrooms.realestatemanager.ui.form

sealed class FormEvent {

    data class GoToPage(val pageToGo: Int) : FormEvent()

    object ExitActivity : FormEvent()

    data class ShowDialog(
        val title: String,
        val message: String,
        val positiveButtonText: String,
        val negativeButtonText: String,
    ) : FormEvent()

    object ShowPicture : FormEvent()
}
