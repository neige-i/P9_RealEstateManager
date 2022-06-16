package com.openclassrooms.realestatemanager.ui.main

import com.openclassrooms.realestatemanager.data.filter.FilterType
import com.openclassrooms.realestatemanager.data.filter.FilterValue

sealed class MainEvent {
    object OpenEstateDetail : MainEvent()
    object OpenEstateForm : MainEvent()
    data class ShowSliderFilterDialog(val filterType: FilterType, val filterValue: FilterValue?) : MainEvent()
    data class ShowCheckableFilterDialog(val filterType: FilterType, val filterValue: FilterValue?) : MainEvent()
    data class ShowCalendarFilterDialog(val filterType: FilterType, val filterValue: FilterValue?) : MainEvent()
}
