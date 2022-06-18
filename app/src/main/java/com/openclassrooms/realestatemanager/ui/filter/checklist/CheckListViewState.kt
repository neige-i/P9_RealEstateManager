package com.openclassrooms.realestatemanager.ui.filter.checklist

import androidx.annotation.StringRes

data class CheckListViewState(
    @StringRes val dialogTitle: Int,
    val items: List<CheckItem>,
) {

    data class CheckItem(
        @StringRes val label: Int,
        val isChecked: Boolean,
        val onClicked: (isChecked: Boolean) -> Unit,
    )
}
