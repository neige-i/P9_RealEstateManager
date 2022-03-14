package com.openclassrooms.realestatemanager.data.form

import androidx.annotation.VisibleForTesting
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FormRepository @Inject constructor() {

    companion object {
        val DEFAULT_FORM = FormEntity(
            type = "",
            typeError = null,
            price = "",
            priceCursor = 0,
            area = "",
            areaCursor = 0,
            totalRoomCount = 0,
            bathroomCount = 0,
            bedroomCount = 0,
            description = "",
            descriptionCursor = 0,
            pictureList = emptyList(),
            pictureListError = null,
            streetName = "",
            streetNameError = null,
            streetNameCursor = 0,
            additionalAddressInfo = "",
            additionalAddressInfoCursor = 0,
            city = "",
            cityError = null,
            cityCursor = 0,
            state = "",
            stateError = null,
            stateCursor = 0,
            zipcode = "",
            zipcodeError = null,
            zipcodeCursor = 0,
            country = "",
            countryError = null,
            countryCursor = 0,
            pointsOfInterests = emptyList(),
            agentName = "",
            marketEntryDate = "",
            marketEntryDateError = null,
            saleDate = "",
            saleDateError = null,
            isAvailableForSale = true
        )
    }

    private val formMutableStateFlow = MutableStateFlow(DEFAULT_FORM)
    private var initialState: FormEntity = DEFAULT_FORM
    private var currentPicturePosition = -1

    fun getFormFlow(): Flow<FormEntity> = formMutableStateFlow

    fun getInitialState(): FormEntity = initialState

    fun getForm(): FormEntity = formMutableStateFlow.value

    fun initForm(form: FormEntity) {
        initialState = form
    }

    @VisibleForTesting
    internal fun setForm(form: FormEntity) {
        formMutableStateFlow.value = form
    }

    fun resetAllErrors() {
        formMutableStateFlow.update {
            it.copy(
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
        }
    }

    fun resetForm() {
        initialState = DEFAULT_FORM
        formMutableStateFlow.value = DEFAULT_FORM
    }

    // MAIN INFO

    fun setType(type: String) {
        formMutableStateFlow.update {
            it.copy(type = type)
        }
    }

    fun setTypeError(typeError: String?) {
        formMutableStateFlow.update {
            it.copy(typeError = typeError)
        }
    }

    fun setPrice(price: String, cursorPosition: Int) {
        formMutableStateFlow.update {
            it.copy(price = price, priceCursor = cursorPosition)
        }
    }

    fun setArea(area: String, cursorPosition: Int) {
        formMutableStateFlow.update {
            it.copy(area = area, areaCursor = cursorPosition)
        }
    }

    fun incTotalRoom() {
        formMutableStateFlow.update {
            it.copy(totalRoomCount = it.totalRoomCount.inc())
        }
    }

    fun decTotalRoom() {
        formMutableStateFlow.update {
            it.copy(totalRoomCount = it.totalRoomCount.dec())
        }
    }

    fun incBathroom() {
        formMutableStateFlow.update {
            it.copy(bathroomCount = it.bathroomCount.inc())
        }
    }

    fun decBathroom() {
        formMutableStateFlow.update {
            it.copy(bathroomCount = it.bathroomCount.dec())
        }
    }

    fun incBedroom() {
        formMutableStateFlow.update {
            it.copy(bedroomCount = it.bedroomCount.inc())
        }
    }

    fun decBedroom() {
        formMutableStateFlow.update {
            it.copy(bedroomCount = it.bedroomCount.dec())
        }
    }

    // DETAIL INFO

    fun setDescription(description: String, cursorPosition: Int) {
        formMutableStateFlow.update {
            it.copy(description = description, descriptionCursor = cursorPosition)
        }
    }

    fun getCurrentPicturePosition(): Int = currentPicturePosition

    fun setCurrentPicturePosition(position: Int) {
        currentPicturePosition = position
    }

    fun setPictureListError(pictureListError: String?) {
        formMutableStateFlow.update {
            it.copy(pictureListError = pictureListError)
        }
    }

    fun addPicture(picture: FormEntity.PictureEntity) {
        modifyPictureList {
            add(picture)
        }
    }

    fun setPictureAt(position: Int, picture: FormEntity.PictureEntity) {
        modifyPictureList {
            set(position, picture)
        }
    }

    fun deletePictureAt(position: Int) {
        modifyPictureList {
            removeAt(position)
        }
    }

    // ADDRESS

    fun setStreetName(streetName: String, cursorPosition: Int) {
        formMutableStateFlow.update {
            it.copy(streetName = streetName, streetNameCursor = cursorPosition)
        }
    }

    fun setStreetNameError(streetNameError: String?) {
        formMutableStateFlow.update {
            it.copy(streetNameError = streetNameError)
        }
    }

    fun setAdditionalAddress(additionalInfo: String, cursorPosition: Int) {
        formMutableStateFlow.update {
            it.copy(
                additionalAddressInfo = additionalInfo,
                additionalAddressInfoCursor = cursorPosition
            )
        }
    }

    fun setCity(city: String, cursorPosition: Int) {
        formMutableStateFlow.update {
            it.copy(city = city, cityCursor = cursorPosition)
        }
    }

    fun setCityError(cityError: String?) {
        formMutableStateFlow.update {
            it.copy(cityError = cityError)
        }
    }

    fun setState(state: String, cursorPosition: Int) {
        formMutableStateFlow.update {
            it.copy(state = state, stateCursor = cursorPosition)
        }
    }

    fun setStateError(stateError: String?) {
        formMutableStateFlow.update {
            it.copy(stateError = stateError)
        }
    }

    fun setZipcode(zipcode: String, cursorPosition: Int) {
        formMutableStateFlow.update {
            it.copy(zipcode = zipcode, zipcodeCursor = cursorPosition)
        }
    }

    fun setZipcodeError(zipcodeError: String?) {
        formMutableStateFlow.update {
            it.copy(zipcodeError = zipcodeError)
        }
    }

    fun setCountry(country: String, cursorPosition: Int) {
        formMutableStateFlow.update {
            it.copy(country = country, countryCursor = cursorPosition)
        }
    }

    fun setCountryError(countryError: String?) {
        formMutableStateFlow.update {
            it.copy(countryError = countryError)
        }
    }

    fun addPoi(labelId: Int) {
        modifyPoiList {
            add(labelId)
        }
    }

    fun removePoi(labelId: Int) {
        modifyPoiList {
            remove(labelId)
        }
    }

    // SALE STATUS

    fun setAgentName(agentName: String) {
        formMutableStateFlow.update {
            it.copy(agentName = agentName)
        }
    }

    fun setEntryDate(entryDate: String) {
        formMutableStateFlow.update {
            it.copy(marketEntryDate = entryDate)
        }
    }

    fun setEntryDateError(entryDateError: String?) {
        formMutableStateFlow.update {
            it.copy(marketEntryDateError = entryDateError)
        }
    }

    fun setSaleDate(saleDate: String) {
        formMutableStateFlow.update {
            it.copy(saleDate = saleDate)
        }
    }

    fun setSaleDateError(saleDateError: String?) {
        formMutableStateFlow.update {
            it.copy(saleDateError = saleDateError)
        }
    }

    fun setAvailability(isAvailable: Boolean) {
        formMutableStateFlow.update {
            it.copy(isAvailableForSale = isAvailable)
        }
    }

    private fun modifyPictureList(modification: MutableList<FormEntity.PictureEntity>.() -> Unit) {
        formMutableStateFlow.update {
            it.copy(pictureList = getModifiedList(it.pictureList) { modification() })
        }
    }

    private fun modifyPoiList(modification: MutableList<Int>.() -> Unit) {
        formMutableStateFlow.update {
            it.copy(pointsOfInterests = getModifiedList(it.pointsOfInterests) { modification() })
        }
    }

    private inline fun <T> getModifiedList(
        list: List<T>,
        modification: MutableList<T>.() -> Unit
    ): MutableList<T> {
        return list.toMutableList().apply { modification(this) }
    }
}