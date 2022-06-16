package com.openclassrooms.realestatemanager.ui.main

import com.openclassrooms.realestatemanager.data.filter.FilterType
import com.openclassrooms.realestatemanager.data.filter.FilterValue

sealed class MainEvent {
    object OpenEstateDetail : MainEvent()
    object OpenEstateForm : MainEvent()
    data class ShowSliderFilterSettings(val filterType: FilterType, val filterValue: FilterValue?) : MainEvent()
    data class ShowCheckListFilterSettings(val filterType: FilterType, val filterValue: FilterValue?) : MainEvent()
    data class ShowDateFilterSettings(val filterType: FilterType, val filterValue: FilterValue?) : MainEvent()
}
