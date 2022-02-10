package com.openclassrooms.realestatemanager.ui.form.main_info

data class MainInfoViewState(
    val selectedType: String,
    val typeError: String?,
    val price: String,
    val area: String,
    val totalRoomCount: String,
    val bathroomCount: String,
    val bedroomCount: String,
)
