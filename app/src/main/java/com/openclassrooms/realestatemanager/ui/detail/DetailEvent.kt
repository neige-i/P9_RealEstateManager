package com.openclassrooms.realestatemanager.ui.detail

sealed class DetailEvent {
    object Exit : DetailEvent()
    object OpenForm : DetailEvent()
}
