package com.openclassrooms.realestatemanager.domain.form

import android.app.Application
import android.net.Uri
import androidx.annotation.StringRes
import com.openclassrooms.realestatemanager.data.PointOfInterest
import com.openclassrooms.realestatemanager.data.RealEstateType
import com.openclassrooms.realestatemanager.data.form.*
import com.openclassrooms.realestatemanager.data.real_estate.RealEstateEntity
import com.openclassrooms.realestatemanager.domain.form.FormType.ADD_ESTATE
import com.openclassrooms.realestatemanager.domain.form.FormType.EDIT_ESTATE
import com.openclassrooms.realestatemanager.domain.real_estate.GetCurrentEstateUseCase
import com.openclassrooms.realestatemanager.domain.real_estate.RealEstateResult
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SetFormUseCase @Inject constructor(
    private val formRepository: FormRepository,
    private val getCurrentEstateUseCase: GetCurrentEstateUseCase,
    private val currentPictureRepository: CurrentPictureRepository,
    private val actionRepository: ActionRepository,
    private val application: Application,
) {

    suspend fun initForm(formType: FormType) {
        when (formType) {
            ADD_ESTATE -> {
                formRepository.setInitialState(FormRepository.DEFAULT_FORM)
                formRepository.setForm(
                    formRepository.getForm().copy(
                        typeError = null,
                        pictureListError = null,
                        streetNameError = null,
                        cityError = null,
                        stateError = null,
                        zipcodeError = null,
                        countryError = null,
                        marketEntryDateError = null,
                        saleDateError = null,
                    )
                )
            }
            EDIT_ESTATE -> {
                val currentEstateResult = getCurrentEstateUseCase.invoke().first()

                if (currentEstateResult is RealEstateResult.Success) {
                    val form = mapEstateToForm(currentEstateResult.realEstate)
                    formRepository.setInitialState(form)
                    formRepository.setForm(form)
                }
            }
        }
    }

    private fun mapEstateToForm(realEstate: RealEstateEntity) = FormEntity(
        id = realEstate.info.realEstateId,
        type = application.getString(RealEstateType.valueOf(realEstate.info.type).labelId),
        typeError = null,
        price = realEstate.info.price?.toString() ?: "",
        priceCursor = 0,
        area = realEstate.info.area?.toString() ?: "",
        areaCursor = 0,
        totalRoomCount = realEstate.info.totalRoomCount,
        bathroomCount = realEstate.info.bathroomCount,
        bedroomCount = realEstate.info.bedroomCount,
        description = realEstate.info.description,
        descriptionCursor = 0,
        pictureList = realEstate.photoList.map {
            FormEntity.PictureEntity(uri = Uri.parse(it.uri), description = it.description)
        },
        pictureListError = null,
        streetName = realEstate.info.streetName,
        streetNameError = null,
        streetNameCursor = 0,
        additionalAddressInfo = realEstate.info.additionalAddressInfo,
        additionalAddressInfoCursor = 0,
        city = realEstate.info.city,
        cityError = null,
        cityCursor = 0,
        state = realEstate.info.state,
        stateError = null,
        stateCursor = 0,
        zipcode = realEstate.info.zipcode,
        zipcodeError = null,
        zipcodeCursor = 0,
        country = realEstate.info.country,
        countryError = null,
        countryCursor = 0,
        pointsOfInterests = realEstate.poiList.map {
            PointOfInterest.valueOf(it.poiValue).labelId
        },
        agentName = realEstate.agent?.username.orEmpty(),
        marketEntryDate = realEstate.info.marketEntryDate,
        marketEntryDateError = null,
        saleDate = realEstate.info.saleDate ?: "",
        saleDateError = null,
        isAvailableForSale = realEstate.info.saleDate == null,
    )

    fun reset() {
        formRepository.resetAll()
    }

    // MAIN INFO

    fun updateType(type: String) {
        formRepository.setForm(formRepository.getForm().copy(type = type))
    }

    fun updatePrice(price: String, cursorPosition: Int) {
        formRepository.setForm(
            formRepository.getForm().copy(price = price, priceCursor = cursorPosition)
        )
    }

    fun updateArea(area: String, cursorPosition: Int) {
        formRepository.setForm(
            formRepository.getForm().copy(area = area, areaCursor = cursorPosition)
        )
    }

    fun incTotalRoomCount() {
        formRepository.setForm(
            formRepository.getForm().let { it.copy(totalRoomCount = it.totalRoomCount.inc()) }
        )
    }

    fun decTotalRoomCount() {
        if (areAnyRoomsLeft()) {
            formRepository.setForm(
                formRepository.getForm().let { it.copy(totalRoomCount = it.totalRoomCount.dec()) }
            )
        }
    }

    fun incBathroomCount() {
        if (areAnyRoomsLeft()) {
            formRepository.setForm(
                formRepository.getForm().let { it.copy(bathroomCount = it.bathroomCount.inc()) }
            )
        }
    }

    fun decBathroomCount() {
        val currentForm = formRepository.getForm()

        if (currentForm.bathroomCount > 0) {
            formRepository.setForm(
                currentForm.copy(bathroomCount = currentForm.bathroomCount.dec())
            )
        }
    }

    fun incBedroomCount() {
        if (areAnyRoomsLeft()) {
            formRepository.setForm(
                formRepository.getForm().let { it.copy(bedroomCount = it.bedroomCount.inc()) }
            )
        }
    }

    fun decBedroomCount() {
        val currentForm = formRepository.getForm()

        if (currentForm.bedroomCount > 0) {
            formRepository.setForm(
                currentForm.copy(bedroomCount = currentForm.bedroomCount.dec())
            )
        }
    }

    private fun areAnyRoomsLeft(): Boolean = formRepository.getForm().run {
        totalRoomCount - bathroomCount - bedroomCount > 0
    }

    // DETAIL INFO

    fun updateDescription(description: String, cursorPosition: Int) {
        formRepository.setForm(
            formRepository.getForm().copy(
                description = description,
                descriptionCursor = cursorPosition
            )
        )
    }

    fun updatePicturePosition(position: Int) {
        formRepository.setCurrentPicturePosition(position)
    }

    fun removePictureAt(position: Int) {
        modifyPictureList {
            removeAt(position)
        }
    }

    fun resetPictureError() {
        formRepository.setForm(formRepository.getForm().copy(pictureListError = null))
    }

    // CURRENT PICTURE

    fun initPicture(uri: Uri, description: String = "") {
        if (currentPictureRepository.getCurrentPicture() == null) {
            actionRepository.requestPictureOpening()
        }

        currentPictureRepository.setPicture(
            CurrentPictureEntity(
                uri = uri,
                description = description,
                descriptionError = null,
                descriptionCursor = 0
            )
        )
    }

    fun updatePictureDescription(description: String, cursorPosition: Int) {
        currentPictureRepository.setPicture(
            currentPictureRepository.getCurrentPicture()?.copy(
                description = description,
                descriptionCursor = cursorPosition
            )
        )
    }

    fun resetPicture() {
        currentPictureRepository.setPicture(null)
    }

    fun savePicture() {
        val currentPicture = currentPictureRepository.getCurrentPicture() ?: return
        val picturePosition = formRepository.getCurrentPicturePosition()

        val picture = FormEntity.PictureEntity(
            uri = currentPicture.uri,
            description = currentPicture.description,
        )

        modifyPictureList {
            if (formRepository.getForm().pictureList.size == picturePosition) {
                add(picture)
            } else {
                set(picturePosition, picture)
            }
        }
    }

    // ADDRESS

    fun updateStreetName(streetName: String, cursorPosition: Int) {
        formRepository.setForm(
            formRepository.getForm().copy(
                streetName = streetName,
                streetNameCursor = cursorPosition
            )
        )
    }

    fun updateAdditionalAddressInfo(additionalAddressInfo: String, cursorPosition: Int) {
        formRepository.setForm(
            formRepository.getForm().copy(
                additionalAddressInfo = additionalAddressInfo,
                additionalAddressInfoCursor = cursorPosition
            )
        )
    }

    fun updateCity(city: String, cursorPosition: Int) {
        formRepository.setForm(
            formRepository.getForm().copy(
                city = city,
                cityCursor = cursorPosition
            )
        )
    }

    fun updateState(state: String, cursorPosition: Int) {
        formRepository.setForm(
            formRepository.getForm().copy(
                state = state,
                stateCursor = cursorPosition
            )
        )
    }

    fun updateZipcode(zipcode: String, cursorPosition: Int) {
        formRepository.setForm(
            formRepository.getForm().copy(
                zipcode = zipcode,
                zipcodeCursor = cursorPosition
            )
        )
    }

    fun updateCountry(country: String, cursorPosition: Int) {
        formRepository.setForm(
            formRepository.getForm().copy(
                country = country,
                countryCursor = cursorPosition
            )
        )
    }

    fun updatePoi(@StringRes labelId: Int, isChecked: Boolean) {
        val updatedForm = formRepository.getForm().let {
            it.copy(
                pointsOfInterests = applyModificationOn(it.pointsOfInterests) {
                    if (isChecked) {
                        add(labelId)
                    } else {
                        remove(labelId)
                    }
                }
            )
        }

        formRepository.setForm(updatedForm)
    }

    // SALE STATUS

    fun updateAgent(agentName: String) {
        formRepository.setForm(
            formRepository.getForm().copy(agentName = agentName)
        )
    }

    fun updateMarketEntryDate(marketEntryDate: String) {
        formRepository.setForm(
            formRepository.getForm().copy(marketEntryDate = marketEntryDate)
        )
    }

    fun updateSaleDate(saleDate: String) {
        formRepository.setForm(
            formRepository.getForm().copy(saleDate = saleDate)
        )
    }

    fun updateAvailability(isAvailable: Boolean) {
        val currentForm = formRepository.getForm()

        formRepository.setForm(
            currentForm.copy(
                isAvailableForSale = isAvailable,
                saleDate = if (!isAvailable) "" else currentForm.saleDate
            )
        )
    }

    private fun modifyPictureList(modification: MutableList<FormEntity.PictureEntity>.() -> Unit) {
        val currentForm = formRepository.getForm()

        formRepository.setForm(
            currentForm.copy(
                pictureList = applyModificationOn(currentForm.pictureList) { modification() }
            )
        )
    }

    private inline fun <T> applyModificationOn(
        list: List<T>,
        modification: MutableList<T>.() -> Unit
    ): MutableList<T> {
        return list.toMutableList().apply { modification(this) }
    }
}