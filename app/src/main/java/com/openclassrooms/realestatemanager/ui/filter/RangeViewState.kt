package com.openclassrooms.realestatemanager.ui.filter

data class RangeViewState(
    val dialogTitle: String,
    val label: String,
    val from: Float,
    val to: Float,
    val min: Float,
    val max: Float,
    val step: Float,
)
