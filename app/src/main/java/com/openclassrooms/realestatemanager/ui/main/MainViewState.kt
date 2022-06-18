package com.openclassrooms.realestatemanager.ui.main

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes

data class MainViewState(
    val toolbarTitle: String,
    @DrawableRes val navigationIconId: Int?,
    val isEditMenuItemVisible: Boolean,
    val isFiltering: Boolean,
    val chips: List<ChipViewState>,
) {

    data class ChipViewState(
        val style: Style,
        val onFilterClicked: () -> Unit,
        val onCloseIconClicked: () -> Unit,
    ) {

        data class Style(
            val text: String,
            @ColorRes val backgroundColor: Int,
            val isCloseIconVisible: Boolean,
        )
    }
}
