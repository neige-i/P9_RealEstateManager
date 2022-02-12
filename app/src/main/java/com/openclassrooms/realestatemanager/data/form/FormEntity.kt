package com.openclassrooms.realestatemanager.data.form

import android.net.Uri

data class FormEntity(
    val type: String,
    val typeError: String?,
    val price: String,
    val area: String,
    val totalRoomCount: Int,
    val bathroomCount: Int,
    val bedroomCount: Int,
    val description: String,
    val pictureUriList: List<Uri?>,
    val streetName: String,
    val streetNameError: String?,
    val additionalAddressInfo: String,
    val city: String,
    val cityError: String?,
    val state: String,
    val stateError: String?,
    val zipcode: String,
    val zipcodeError: String?,
    val country: String,
    val countryError: String?,
    val pointsOfInterests: List<Int>,
    val agentName: String,
    val marketEntryDate: String,
    val marketEntryDateError: String?,
    val saleDate: String,
    val saleDateError: String?,
    val isAvailableForSale: Boolean,
)
