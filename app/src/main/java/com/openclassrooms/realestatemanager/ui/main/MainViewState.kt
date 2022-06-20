package com.openclassrooms.realestatemanager.ui.main

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class MainViewState(
    val toolbar: Toolbar,
    val isEditMenuItemVisible: Boolean,
    val chips: List<FilterChipViewState>,
) {
    data class Toolbar(
        @StringRes val title: Int,
        @DrawableRes val navIcon: Int?,
        val isFiltering: Boolean,
    )
}
