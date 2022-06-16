package com.openclassrooms.realestatemanager.ui.filter

import androidx.annotation.StringRes

data class MultiChoiceViewState(
    @StringRes val dialogTitle: Int,
    val checkItems: List<CheckItem>,
) {

    data class CheckItem(
        @StringRes val label: Int,
        val isChecked: Boolean,
        val onClicked: (isChecked: Boolean) -> Unit,
    )
}
