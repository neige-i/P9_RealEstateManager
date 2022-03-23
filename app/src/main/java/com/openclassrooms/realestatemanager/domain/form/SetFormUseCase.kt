package com.openclassrooms.realestatemanager.domain.form

import android.app.Application
import android.net.Uri
import androidx.annotation.StringRes
import com.openclassrooms.realestatemanager.data.PointOfInterest
import com.openclassrooms.realestatemanager.data.RealEstateType
import com.openclassrooms.realestatemanager.data.agent.AgentRepository
import com.openclassrooms.realestatemanager.data.form.ActionRepository
import com.openclassrooms.realestatemanager.data.form.CurrentPictureRepository
import com.openclassrooms.realestatemanager.data.form.FormEntity
import com.openclassrooms.realestatemanager.data.form.FormRepository
import com.openclassrooms.realestatemanager.data.real_estate.RealEstateEntity
import javax.inject.Inject

class SetFormUseCase @Inject constructor(
    private val formRepository: FormRepository,
    private val currentPictureRepository: CurrentPictureRepository,
    private val agentRepository: AgentRepository,
    private val actionRepository: ActionRepository,
    private val application: Application,
) {

    fun initForm(realEstateEntity: RealEstateEntity? = null) {
        if (realEstateEntity == null) {
            formRepository.initForm(FormRepository.DEFAULT_FORM)
            formRepository.resetAllErrors()
        } else {
            val form = mapEstateToForm(realEstateEntity)
            formRepository.initForm(form)
            formRepository.setForm(form)
        }
    }

    private fun mapEstateToForm(realEstate: RealEstateEntity) = FormEntity(
        id = realEstate.id,
        type = application.getString(RealEstateType.valueOf(realEstate.type).labelId),
        typeError = null,
        price = realEstate.price?.toString() ?: "",
        priceCursor = 0,
        area = realEstate.area?.toString() ?: "",
        areaCursor = 0,
        totalRoomCount = realEstate.totalRoomCount,
        bathroomCount = realEstate.bathroomCount,
        bedroomCount = realEstate.bedroomCount,
        description = realEstate.description,
        descriptionCursor = 0,
        pictureList = realEstate.pictureList.map {
            FormEntity.PictureEntity(uri = Uri.parse(it.key), description = it.value)
        },
        pictureListError = null,
        streetName = realEstate.streetName,
        streetNameError = null,
        streetNameCursor = 0,
        additionalAddressInfo = realEstate.additionalAddressInfo,
        additionalAddressInfoCursor = 0,
        city = realEstate.city,
        cityError = null,
        cityCursor = 0,
        state = realEstate.state,
        stateError = null,
        stateCursor = 0,
        zipcode = realEstate.zipcode,
        zipcodeError = null,
        zipcodeCursor = 0,
        country = realEstate.country,
        countryError = null,
        countryCursor = 0,
        pointsOfInterests = realEstate.pointsOfInterests.map {
            PointOfInterest.valueOf(it).labelId
        },
        agentName = realEstate.agentId?.let {
            agentRepository.getAgentById(it)?.name
        } ?: "",
        marketEntryDate = realEstate.marketEntryDate,
        marketEntryDateError = null,
        saleDate = realEstate.saleDate ?: "",
        saleDateError = null,
        isAvailableForSale = realEstate.saleDate == null,
    )

    fun reset() {
        formRepository.resetForm()
    }

    // MAIN INFO

    fun updateType(type: String) {
        formRepository.setType(type)
    }

    fun updatePrice(price: String, cursorPosition: Int) {
        formRepository.setPrice(price, cursorPosition)
    }

    fun updateArea(area: String, cursorPosition: Int) {
        formRepository.setArea(area, cursorPosition)
    }

    fun incTotalRoomCount() {
        formRepository.incTotalRoom()
    }

    fun decTotalRoomCount() {
        if (areAnyRoomsLeft()) {
            formRepository.decTotalRoom()
        }
    }

    fun incBathroomCount() {
        if (areAnyRoomsLeft()) {
            formRepository.incBathroom()
        }
    }

    fun decBathroomCount() {
        if (formRepository.getForm().bathroomCount > 0) {
            formRepository.decBathroom()
        }
    }

    fun incBedroomCount() {
        if (areAnyRoomsLeft()) {
            formRepository.incBedroom()
        }
    }

    fun decBedroomCount() {
        if (formRepository.getForm().bedroomCount > 0) {
            formRepository.decBedroom()
        }
    }

    private fun areAnyRoomsLeft(): Boolean = formRepository.getForm().run {
        totalRoomCount - bathroomCount - bedroomCount > 0
    }

    // DETAIL INFO

    fun updateDescription(description: String, cursorPosition: Int) {
        formRepository.setDescription(description, cursorPosition)
    }

    fun updatePicturePosition(position: Int) {
        formRepository.setCurrentPicturePosition(position)
    }

    fun removePictureAt(position: Int) {
        formRepository.deletePictureAt(position)
    }

    fun resetPictureError() {
        formRepository.setPictureListError(null)
    }

    // CURRENT PICTURE

    fun setPicture(pictureUri: Uri, pictureDescription: String) {
        currentPictureRepository.initPicture(pictureUri, pictureDescription)
        actionRepository.requestPictureOpening()
    }

    fun setPictureUri(uri: Uri) {
        if (currentPictureRepository.getCurrentPicture() == null) {
            setPicture(uri, "")
        } else {
            currentPictureRepository.setUri(uri)
        }
    }

    fun updatePictureDescription(description: String, cursorPosition: Int) {
        currentPictureRepository.setDescription(description, cursorPosition)
    }

    fun resetPicture() {
        currentPictureRepository.reset()
    }

    fun savePicture() {
        val currentPicture = currentPictureRepository.getCurrentPicture() ?: return
        val picturePosition = formRepository.getCurrentPicturePosition()

        val picture = FormEntity.PictureEntity(
            uri = currentPicture.uri,
            description = currentPicture.description,
        )

        if (formRepository.getForm().pictureList.size == picturePosition) {
            formRepository.addPicture(picture)
        } else {
            formRepository.setPictureAt(picturePosition, picture)
        }
    }

    // ADDRESS

    fun updateStreetName(streetName: String, cursorPosition: Int) {
        formRepository.setStreetName(streetName, cursorPosition)
    }

    fun updateAdditionalAddressInfo(additionalAddressInfo: String, cursorPosition: Int) {
        formRepository.setAdditionalAddress(additionalAddressInfo, cursorPosition)
    }

    fun updateCity(city: String, cursorPosition: Int) {
        formRepository.setCity(city, cursorPosition)
    }

    fun updateState(state: String, cursorPosition: Int) {
        formRepository.setState(state.uppercase(), cursorPosition)
    }

    fun updateZipcode(zipcode: String, cursorPosition: Int) {
        formRepository.setZipcode(zipcode, cursorPosition)
    }

    fun updateCountry(country: String, cursorPosition: Int) {
        formRepository.setCountry(country, cursorPosition)
    }

    fun updatePoi(@StringRes labelId: Int, isChecked: Boolean) {
        if (isChecked) {
            formRepository.addPoi(labelId)
        } else {
            formRepository.removePoi(labelId)
        }
    }

    // SALE STATUS

    fun updateAgent(agentName: String) {
        formRepository.setAgentName(agentName)
    }

    fun updateMarketEntryDate(marketEntryDate: String) {
        formRepository.setEntryDate(marketEntryDate)
    }

    fun updateSaleDate(saleDate: String) {
        formRepository.setSaleDate(saleDate)
    }

    fun updateAvailability(isAvailable: Boolean) {
        formRepository.setAvailability(isAvailable)

        if (!isAvailable) {
            formRepository.setSaleDate("")
        }
    }
}