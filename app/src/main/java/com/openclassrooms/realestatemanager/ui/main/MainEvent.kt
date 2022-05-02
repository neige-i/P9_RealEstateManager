package com.openclassrooms.realestatemanager.ui.main

sealed class MainEvent {
    object OpenEstateDetail : MainEvent()
    object OpenEstateForm : MainEvent()
}
