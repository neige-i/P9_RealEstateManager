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
    }

    private val defaultRealEstateEntity = FormEntity(
        type = "",
        typeError = null,
        price = "",
        area = "",
        totalRoomCount = 0,
        bathroomCount = 0,
        bedroomCount = 0,
        description = "",
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

    private var currentForm = defaultRealEstateEntity
    private val formMutableLiveData = MutableLiveData(currentForm)

    fun getCurrentForm() = currentForm

    fun getForm(): LiveData<FormEntity> = Transformations.distinctUntilChanged(formMutableLiveData)

    fun setForm(form: FormEntity) {
        currentForm = form
        formMutableLiveData.value = form
    }
}