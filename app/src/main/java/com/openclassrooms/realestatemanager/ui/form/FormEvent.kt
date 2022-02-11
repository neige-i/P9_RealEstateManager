package com.openclassrooms.realestatemanager.ui.form

sealed class FormEvent {

    data class GoToPage(val pageToGo: Int) : FormEvent()

    object ExitActivity : FormEvent()

    data class ShowExitDialog(val dialogMessage: String) : FormEvent()
}
