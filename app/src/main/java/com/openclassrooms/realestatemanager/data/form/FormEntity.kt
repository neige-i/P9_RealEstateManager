package com.openclassrooms.realestatemanager.data.form

import android.net.Uri
import com.openclassrooms.realestatemanager.data.PointOfInterest
import com.openclassrooms.realestatemanager.data.RealEstateType
import java.time.LocalDate

data class FormEntity(
    val id: Long,
    val estateType: RealEstateType?,
    val typeError: String?,
    val price: String,
    val area: String,
    val totalRoomCount: Int,
    val bathroomCount: Int,
    val bedroomCount: Int,
    val description: String,
    val pictureList: List<PictureEntity>,
    val pictureListError: String?,
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
    val pointsOfInterests: List<PointOfInterest>,
    val agentName: String,
    val marketEntryDate: LocalDate?,
    val marketEntryDateError: String?,
    val saleDate: LocalDate?,
    val saleDateError: String?,
    val isAvailableForSale: Boolean,
) {

    data class PictureEntity(
        val uri: Uri,
        val description: String,
    )
}
