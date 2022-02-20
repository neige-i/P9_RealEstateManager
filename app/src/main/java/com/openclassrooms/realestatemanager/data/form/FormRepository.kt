package com.openclassrooms.realestatemanager.data.form

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    private val formMutableLiveData = MutableLiveData<FormEntity>()
    private var initialState: FormEntity? = null
    private var currentState: FormEntity? = null
    private var currentPicturePosition = -1

    fun getFormLiveData(): LiveData<FormEntity> = formMutableLiveData

    fun getInitialState(): FormEntity = initialState
        ?: throw IllegalStateException("The form has not been initialized. Please call initForm() before accessing it")

    fun getNonNullForm(): FormEntity = currentState
        ?: throw IllegalStateException("The form has not been set. Please call setForm() before accessing it")

    fun getForm(): FormEntity? = currentState

    fun initForm(form: FormEntity) {
        initialState = form
    }

    fun setForm(form: FormEntity) {
        currentState = form
        formMutableLiveData.value = form
    }

    fun resetForm() {
        initialState = null
        currentState = null
    }

    fun getCurrentPicturePosition(): Int = currentPicturePosition

    fun setCurrentPicturePosition(position: Int) {
        currentPicturePosition = position
    }
}