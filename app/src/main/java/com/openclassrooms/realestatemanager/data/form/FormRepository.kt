package com.openclassrooms.realestatemanager.data.form

import kotlinx.coroutines.flow.Flow
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
            area = "",
            totalRoomCount = 0,
            bathroomCount = 0,
            bedroomCount = 0,
            description = "",
            pictureList = emptyList(),
            pictureListError = null,
            streetName = "",
            streetNameError = null,
            additionalAddressInfo = "",
            city = "",
            cityError = null,
            state = "",
            stateError = null,
            zipcode = "",
            zipcodeError = null,
            country = "",
            countryError = null,
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
}