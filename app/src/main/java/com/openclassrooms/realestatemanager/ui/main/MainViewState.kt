package com.openclassrooms.realestatemanager.ui.main

import androidx.annotation.DrawableRes

data class MainViewState(
    val toolbarTitle: String,
    @DrawableRes val navigationIconId: Int?,
    val isEditMenuItemVisible: Boolean,
)
