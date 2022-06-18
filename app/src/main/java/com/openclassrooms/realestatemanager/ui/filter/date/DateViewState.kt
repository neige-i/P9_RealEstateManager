package com.openclassrooms.realestatemanager.ui.filter.date

import androidx.annotation.IdRes
import androidx.annotation.StringRes

data class DateViewState(
    @StringRes val dialogTitle: Int,
    @IdRes val selectedRadioBtn: Int,
    val isDateInputVisible: Boolean,
    val isStartDateEndIconVisible: Boolean,
    val startDateInputText: String,
    val isEndDateEndIconVisible: Boolean,
    val endDateInputText: String,
)
