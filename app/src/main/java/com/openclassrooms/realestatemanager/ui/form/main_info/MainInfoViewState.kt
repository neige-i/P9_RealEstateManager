package com.openclassrooms.realestatemanager.ui.form.main_info

import androidx.annotation.StringRes
import com.openclassrooms.realestatemanager.ui.util.LocalText

data class MainInfoViewState(
    @StringRes val selectedType: LocalText,
    val typeError: String?,
    val price: String,
    val priceSelection: Int,
    val area: String,
    val areaSelection: Int,
    val totalRoomCount: String,
    val bathroomCount: String,
    val bedroomCount: String,
)
