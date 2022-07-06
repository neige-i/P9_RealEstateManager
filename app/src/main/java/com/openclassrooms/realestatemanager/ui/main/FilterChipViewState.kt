package com.openclassrooms.realestatemanager.ui.main

import androidx.annotation.ColorRes
import com.openclassrooms.realestatemanager.ui.util.LocalText

data class FilterChipViewState(
    val style: Style,
    val onClicked: () -> Unit,
    val onCloseIconClicked: () -> Unit,
) {

    data class Style(
        val text: LocalText,
        @ColorRes val backgroundColor: Int,
        val isCloseIconVisible: Boolean,
    )
}
