package com.openclassrooms.realestatemanager.ui.main

sealed class MainEvent {
    object GoToDetailActivity : MainEvent()
    object GoToFormActivity : MainEvent()
}
