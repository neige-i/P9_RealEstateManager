package com.openclassrooms.realestatemanager.ui.filter

data class MultiChoiceViewState(
    val dialogTitle: String,
    val labels: List<String>,
    val checkedItems: List<Boolean>,
)
