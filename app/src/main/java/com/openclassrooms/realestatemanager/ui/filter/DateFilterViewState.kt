package com.openclassrooms.realestatemanager.ui.filter

import androidx.annotation.IdRes

data class DateFilterViewState(
    @IdRes val selectedRadioBtn: Int,
    val isDateInputVisible: Boolean,
    val isStartDateEndIconVisible: Boolean,
    val startDateInputText: String,
    val isEndDateEndIconVisible: Boolean,
    val endDateInputText: String,
)
