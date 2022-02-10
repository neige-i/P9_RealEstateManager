package com.openclassrooms.realestatemanager.data.form

data class FormEntity(
    val type: String,
    val price: String,
    val area: String,
    val totalRoomCount: Int,
    val bathroomCount: Int,
    val bedroomCount: Int,
    val description: String,
    val streetNameHouseNumber: String,
    val additionalAddressInfo: String,
    val city: String,
    val state: String,
    val stateError: String?,
    val zipcode: String,
    val zipcodeError: String?,
    val country: String,
    val pointsOfInterests: List<Int>,
    val agentName: String,
    val marketEntryDate: String,
    val saleDate: String,
    val isAvailableForSale: Boolean,
)
