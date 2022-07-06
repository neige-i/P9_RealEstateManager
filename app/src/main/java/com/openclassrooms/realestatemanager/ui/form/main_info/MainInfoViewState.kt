package com.openclassrooms.realestatemanager.ui.form.main_info

import androidx.annotation.StringRes
import com.openclassrooms.realestatemanager.ui.util.LocalText

data class MainInfoViewState(
    @StringRes val selectedType: LocalText,
    val typeError: String?,
    val price: String,
    val area: String,
    val totalRoomCount: String,
    val bathroomCount: String,
    val bedroomCount: String,
)
