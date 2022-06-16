package com.openclassrooms.realestatemanager.ui.filter

import androidx.annotation.IdRes
import androidx.annotation.StringRes

data class DateFilterViewState(
    @StringRes val dialogTitle: Int,
    @IdRes val selectedRadioBtn: Int,
    val isDateInputVisible: Boolean,
    val isStartDateEndIconVisible: Boolean,
    val startDateInputText: String,
    val isEndDateEndIconVisible: Boolean,
    val endDateInputText: String,
)
