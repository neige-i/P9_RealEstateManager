package com.openclassrooms.realestatemanager.ui.main

import com.openclassrooms.realestatemanager.data.filter.FilterType
import com.openclassrooms.realestatemanager.data.filter.FilterValue

sealed class MainEvent {

    object OpenEstateDetail : MainEvent()
    object OpenEstateForm : MainEvent()

    data class OpenSliderFilterForm(override val filterType: FilterType, override val filterValue: FilterValue?) : OpenFilterForm()
    data class OpenCheckListFilterForm(override val filterType: FilterType, override val filterValue: FilterValue?) : OpenFilterForm()
    data class OpenDateFilterForm(override val filterType: FilterType, override val filterValue: FilterValue?) : OpenFilterForm()

    // ----------

    sealed class OpenFilterForm : MainEvent() {
        abstract val filterType: FilterType
        abstract val filterValue: FilterValue?
    }
}
