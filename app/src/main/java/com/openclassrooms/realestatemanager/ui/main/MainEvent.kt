package com.openclassrooms.realestatemanager.ui.main

import com.openclassrooms.realestatemanager.data.filter.FilterType
import com.openclassrooms.realestatemanager.data.filter.FilterValue

sealed class MainEvent {
    object OpenEstateDetail : MainEvent()
    object OpenEstateForm : MainEvent()
    data class ShowSliderFilterDialog(val filterType: FilterType.Slider, val minMaxFilterValue: FilterValue.MinMax<*>?) : MainEvent()
    data class ShowCheckableFilterDialog(val filterType: FilterType.CheckList, val choicesFilterValue: FilterValue.Choices?) : MainEvent()
    data class ShowCalendarFilterDialog(val availableDatesFilterValue: FilterValue.AvailableDates?) : MainEvent()
}
