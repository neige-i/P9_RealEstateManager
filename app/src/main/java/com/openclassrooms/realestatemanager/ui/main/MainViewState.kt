package com.openclassrooms.realestatemanager.ui.main

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.openclassrooms.realestatemanager.ui.util.LocalText

data class MainViewState(
    val toolbar: Toolbar,
    val isEditMenuItemVisible: Boolean,
    val chips: List<ChipViewState>,
) {
    data class Toolbar(
        @StringRes val title: Int,
        @DrawableRes val navIcon: Int?,
        val isFiltering: Boolean,
    )

    data class ChipViewState(
        val style: Style,
        val onFilterClicked: () -> Unit,
        val onCloseIconClicked: () -> Unit,
    ) {

        data class Style(
            val text: LocalText,
            @ColorRes val backgroundColor: Int,
            val isCloseIconVisible: Boolean,
        )
    }
}
