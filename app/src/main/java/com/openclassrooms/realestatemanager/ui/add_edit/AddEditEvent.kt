package com.openclassrooms.realestatemanager.ui.add_edit

sealed class AddEditEvent {

    data class GoToPage(val pageToGo: Int) : AddEditEvent()

    object ExitActivity : AddEditEvent()
}
