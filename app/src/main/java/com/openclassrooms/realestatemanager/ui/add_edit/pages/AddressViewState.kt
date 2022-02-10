package com.openclassrooms.realestatemanager.ui.add_edit.pages

import androidx.annotation.StringRes

data class AddressViewState(
    val streetNumber: String,
    val additionalInfo: String,
    val city: String,
    val state: String,
    val zipcode: String,
    val country: String,
    val pointOfInterestList: List<ChipViewState>,
) {

    data class ChipViewState(
        @StringRes val labelId: Int,
        val isSelected: Boolean,
    )
}
