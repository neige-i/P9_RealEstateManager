package com.openclassrooms.realestatemanager.data.form

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FormRepository @Inject constructor() {

    private val defaultRealEstateEntity = FormEntity(
        type = "",
        price = "",
        area = "",
        totalRoomCount = 0,
        bathroomCount = 0,
        bedroomCount = 0,
        description = "",
        streetNameHouseNumber = "",
        additionalAddressInfo = "",
        city = "",
        state = "",
        zipcode = "",
        country = "",
        pointsOfInterests = emptyList(),
        agentName = "",
        marketEntryDate = "",
        saleDate = "",
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