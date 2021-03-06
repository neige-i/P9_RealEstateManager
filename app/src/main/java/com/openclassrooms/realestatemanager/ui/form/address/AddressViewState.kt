package com.openclassrooms.realestatemanager.ui.form.address

import androidx.annotation.StringRes

data class AddressViewState(
    val streetNumber: String,
    val streetNumberError: String?,
    val additionalInfo: String,
    val city: String,
    val cityError: String?,
    val state: String,
    val stateError: String?,
    val zipcode: String,
    val zipcodeError: String?,
    val country: String,
    val countryError: String?,
    val pointOfInterestList: List<ChipViewState>,
) {

    data class ChipViewState(
        @StringRes val labelId: Int,
        val isSelected: Boolean,
    )
}
