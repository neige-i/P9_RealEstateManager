package com.openclassrooms.realestatemanager.ui.filter

data class ShowDatePickerEvent(
    val selectedDate: Long?,
    val minConstraint: Long?,
    val maxConstraint: Long?,
    val onValidated: (dateMillis: Long?) -> Unit,
)
