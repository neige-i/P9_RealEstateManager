package com.openclassrooms.realestatemanager.ui.main

sealed class MainEvent {
    object OpenEstateDetail : MainEvent()
    object OpenEstateForm : MainEvent()
    object ShowSliderFilterDialog : MainEvent()
    object ShowCheckableFilterDialog : MainEvent()
    object ShowCalendarFilterDialog : MainEvent()
}
