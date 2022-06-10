package com.openclassrooms.realestatemanager.data.form

import android.net.Uri
import com.openclassrooms.realestatemanager.data.RealEstateType
import com.openclassrooms.realestatemanager.data.PointOfInterest
import java.time.LocalDate

data class FormEntity(
    val id: Long,
    val estateType: RealEstateType?,
    val typeError: String?,
    val price: String,
    val priceCursor: Int,
    val area: String,
    val areaCursor: Int,
    val totalRoomCount: Int,
    val bathroomCount: Int,
    val bedroomCount: Int,
    val description: String,
    val descriptionCursor: Int,
    val pictureList: List<PictureEntity>,
    val pictureListError: String?,
    val streetName: String,
    val streetNameError: String?,
    val streetNameCursor: Int,
    val additionalAddressInfo: String,
    val additionalAddressInfoCursor: Int,
    val city: String,
    val cityError: String?,
    val cityCursor: Int,
    val state: String,
    val stateError: String?,
    val stateCursor: Int,
    val zipcode: String,
    val zipcodeError: String?,
    val zipcodeCursor: Int,
    val country: String,
    val countryError: String?,
    val countryCursor: Int,
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
