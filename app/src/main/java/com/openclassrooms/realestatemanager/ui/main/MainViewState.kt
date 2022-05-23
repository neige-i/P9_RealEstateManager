package com.openclassrooms.realestatemanager.ui.main

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes

data class MainViewState(
    val toolbarTitle: String,
    @DrawableRes val navigationIconId: Int?,
    val isEditMenuItemVisible: Boolean,
    val isFiltering: Boolean,
    val filterList: List<FilterViewState>,
) {

    data class FilterViewState(
        val text: String,
        @ColorRes val backgroundColor: Int,
        val isCloseIconVisible: Boolean,
        val onFilterClicked: () -> Unit,
        val onCloseIconClicked: () -> Unit,
    )
}
