package com.openclassrooms.realestatemanager.ui.form.address

import androidx.annotation.StringRes

data class AddressViewState(
    val streetNumber: String,
    val streetNumberError: String?,
    val streetNumberSelection: Int,
    val additionalInfo: String,
    val additionalInfoSelection: Int,
    val city: String,
    val cityError: String?,
    val citySelection: Int,
    val state: String,
    val stateError: String?,
    val stateSelection: Int,
    val zipcode: String,
    val zipcodeError: String?,
    val zipcodeSelection: Int,
    val country: String,
    val countryError: String?,
    val countrySelection: Int,
    val pointOfInterestList: List<ChipViewState>,
) {

    data class ChipViewState(
        @StringRes val labelId: Int,
        val isSelected: Boolean,
    )
}
