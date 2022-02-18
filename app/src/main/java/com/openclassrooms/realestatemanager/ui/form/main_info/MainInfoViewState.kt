package com.openclassrooms.realestatemanager.ui.form.main_info

data class MainInfoViewState(
    val selectedType: String,
    val typeError: String?,
    val price: String,
    val priceSelection: Int,
    val area: String,
    val areaSelection: Int,
    val totalRoomCount: String,
    val bathroomCount: String,
    val bedroomCount: String,
)
