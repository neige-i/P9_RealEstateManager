package com.openclassrooms.realestatemanager.ui.form

import androidx.annotation.StringRes
import com.openclassrooms.realestatemanager.ui.util.LocalText

sealed class FormEvent {

    object ExitActivity : FormEvent()

    data class GoToPage(
        val pageToGo: Int,
    ) : FormEvent()

    data class ShowDialog(
        val type: FormViewModel.DialogType,
        @StringRes val title: Int,
        val message: LocalText,
        val positiveButtonText: String,
        val negativeButtonText: String,
    ) : FormEvent()

    object ShowPicture : FormEvent()
}
