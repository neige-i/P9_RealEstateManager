package com.openclassrooms.realestatemanager.data.form

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FormRepository @Inject constructor() {

    companion object {
        val STATE_POSTAL_ABBR = listOf(
            "AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "DC", "FL", "GA", "HI", "ID", "IL",
            "IN", "IA", "KS", "KY", "LA", "ME", "MD", "MA", "MI", "MN", "MS", "MO", "MT", "NE",
            "NV", "NH", "NJ", "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "PR", "RI", "SC",
            "SD", "TN", "TX", "UT", "VT", "VA", "VI", "WA", "WV", "WI", "WY",
        )

        val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        private val DEFAULT_FORM = FormEntity(
            type = "",
            typeError = null,
            price = "",
            area = "",
            totalRoomCount = 0,
            bathroomCount = 0,
            bedroomCount = 0,
            description = "",
            pictureList = emptyList(),
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
    private var positionOfPictureToUpdate = -1

    fun getFormLiveData(): LiveData<FormEntity> {
        return Transformations.distinctUntilChanged(formMutableLiveData)
    }

    fun getCurrentForm(): FormEntity = currentState ?: throw NullPointerException(
        "The form has not been initialized. Please call initForm() before accessing it"
    )

    fun getFormType(): FormType = if (initialState == DEFAULT_FORM) FormType.ADD else FormType.EDIT

    fun containsModifications(): Boolean = currentState != initialState

    fun initForm(form: FormEntity? = null) {
        initialState = form ?: DEFAULT_FORM
        setForm(form ?: currentState ?: DEFAULT_FORM)
    }

    fun setForm(form: FormEntity) {
        currentState = form
        formMutableLiveData.value = form
    }

    fun resetForm() {
        initialState = null
        currentState = null
    }

    fun getPositionOfPictureToUpdate(): Int = positionOfPictureToUpdate

    fun setPositionOfPictureToUpdate(position: Int) {
        positionOfPictureToUpdate = position
    }

    enum class FormType {
        ADD,
        EDIT,
    }
}