package com.openclassrooms.realestatemanager.ui.form

sealed class FormEvent {

    object ExitActivity : FormEvent()

    data class GoToPage(
        val pageToGo: Int,
    ) : FormEvent()

    data class ShowDialog(
        val type: FormViewModel.DialogType,
        val title: String,
        val message: String,
        val positiveButtonText: String,
        val negativeButtonText: String,
    ) : FormEvent()

    object ShowPicture : FormEvent()
}
