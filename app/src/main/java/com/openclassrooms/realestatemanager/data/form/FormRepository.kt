package com.openclassrooms.realestatemanager.data.form

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FormRepository @Inject constructor() {

    companion object {
        val DEFAULT_FORM = FormEntity(
            id = 0,
            estateType = null,
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
            marketEntryDate = null,
            marketEntryDateError = null,
            saleDate = null,
            saleDateError = null,
            isAvailableForSale = true
        )
    }

    private val formMutableStateFlow = MutableStateFlow(DEFAULT_FORM)
    private var initialState: FormEntity = DEFAULT_FORM
    private var currentPicturePosition = -1
    private val imagePickerMutableSharedFlow = MutableSharedFlow<ImagePicker>(replay = 1)

    fun getFormFlow(): Flow<FormEntity> = formMutableStateFlow

    fun getForm(): FormEntity = formMutableStateFlow.value

    fun setForm(form: FormEntity) {
        formMutableStateFlow.value = form
    }

    fun getInitialState(): FormEntity = initialState

    fun setInitialState(initialForm: FormEntity) {
        initialState = initialForm
    }

    fun resetAll() {
        initialState = DEFAULT_FORM
        formMutableStateFlow.value = DEFAULT_FORM
    }

    fun getCurrentPicturePosition(): Int = currentPicturePosition

    fun setCurrentPicturePosition(position: Int) {
        currentPicturePosition = position
    }

    fun getImagePickerFlow(): Flow<ImagePicker> = imagePickerMutableSharedFlow

    fun setImagePicker(imagePicker: ImagePicker) {
        imagePickerMutableSharedFlow.tryEmit(imagePicker)
    }
}