package com.openclassrooms.realestatemanager.ui.form

import androidx.annotation.StringRes
import com.openclassrooms.realestatemanager.ui.util.LocalText

data class FormViewState(
    val toolbarTitle: LocalText,
    @StringRes val submitButtonText: Int,
)
