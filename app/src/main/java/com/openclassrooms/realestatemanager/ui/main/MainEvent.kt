package com.openclassrooms.realestatemanager.ui.main

sealed class MainEvent() {
    object GoToDetailActivity : MainEvent()
    object GoToAddActivity : MainEvent()
}
