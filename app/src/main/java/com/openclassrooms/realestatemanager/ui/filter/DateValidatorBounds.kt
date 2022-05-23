package com.openclassrooms.realestatemanager.ui.filter

import android.os.Parcelable
import com.google.android.material.datepicker.CalendarConstraints
import kotlinx.parcelize.Parcelize

@Parcelize
class DateValidatorBounds(
    private val from: Long?,
    private val until: Long?,
) : CalendarConstraints.DateValidator, Parcelable {

    override fun isValid(date: Long): Boolean = when {
        from != null && until != null -> date in from..until
        from != null -> date >= from
        until != null -> date <= until
        else -> true
    }
}