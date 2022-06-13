package com.openclassrooms.realestatemanager.ui.filter

import androidx.annotation.StringRes

data class MultiChoiceViewState(
    @StringRes val dialogTitle: Int,
    val labels: List<String>,
    val checkedItems: List<Boolean>,
)
