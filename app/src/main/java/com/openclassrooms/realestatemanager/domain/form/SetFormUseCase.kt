package com.openclassrooms.realestatemanager.domain.form

import androidx.annotation.StringRes
import com.openclassrooms.realestatemanager.data.form.FormRepository
import javax.inject.Inject

class SetFormUseCase @Inject constructor(
    private val formRepository: FormRepository,
) {

    fun updateType(type: String) {
        formRepository.setForm(getForm().copy(type = type))
    }

    fun updatePrice(price: String) {
        formRepository.setForm(getForm().copy(price = price))
    }

    fun updateArea(area: String) {
        formRepository.setForm(getForm().copy(area = area))
    }

    fun incTotalRoomCount() {
        formRepository.setForm(getForm().copy(totalRoomCount = getForm().totalRoomCount.inc()))
    }

    fun decTotalRoomCount() {
        if (areAnyRoomsLeft()) {
            formRepository.setForm(getForm().copy(totalRoomCount = getForm().totalRoomCount.dec()))
        }
    }

    fun incBathroomCount() {
        if (areAnyRoomsLeft()) {
            formRepository.setForm(getForm().copy(bathroomCount = getForm().bathroomCount.inc()))
        }
    }

    fun decBathroomCount() {
        if (getForm().bathroomCount > 0) {
            formRepository.setForm(getForm().copy(bathroomCount = getForm().bathroomCount.dec()))
        }
    }

    fun incBedroomCount() {
        if (areAnyRoomsLeft()) {
            formRepository.setForm(getForm().copy(bedroomCount = getForm().bedroomCount.inc()))
        }
    }

    fun decBedroomCount() {
        if (getForm().bedroomCount > 0) {
            formRepository.setForm(getForm().copy(bedroomCount = getForm().bedroomCount.dec()))
        }
    }

    private fun areAnyRoomsLeft(): Boolean = getForm().run {
        totalRoomCount - bathroomCount - bedroomCount > 0
    }

    fun updateDescription(description: String) {
        formRepository.setForm(getForm().copy(description = description))
    }

    fun updateStreetNameHouseNumber(streetNameHouseNumber: String) {
        formRepository.setForm(getForm().copy(streetNameHouseNumber = streetNameHouseNumber))
    }

    fun updateAdditionalAddressInfo(additionalAddressInfo: String) {
        formRepository.setForm(getForm().copy(additionalAddressInfo = additionalAddressInfo))
    }

    fun updateCity(city: String) {
        formRepository.setForm(getForm().copy(city = city))
    }

    fun updateState(state: String) {
        formRepository.setForm(getForm().copy(state = state.uppercase()))
    }

    fun updateZipcode(zipcode: String) {
        formRepository.setForm(getForm().copy(zipcode = zipcode))
    }

    fun updateCountry(country: String) {
        formRepository.setForm(getForm().copy(country = country))
    }

    fun updatePoi(@StringRes labelId: Int, isChecked: Boolean) {
        val poiList = getForm().pointsOfInterests.toMutableList()

        if (isChecked) {
            poiList.add(labelId)
        } else {
            poiList.remove(labelId)
        }

        formRepository.setForm(getForm().copy(pointsOfInterests = poiList))
    }

    fun updateAgent(agentName: String) {
        formRepository.setForm(getForm().copy(agentName = agentName))
    }

    fun updateMarketEntryDate(marketEntryDate: String) {
        formRepository.setForm(getForm().copy(marketEntryDate = marketEntryDate))
    }

    fun updateSaleDate(saleDate: String) {
        formRepository.setForm(getForm().copy(saleDate = saleDate))
    }

    fun updateAvailability(isAvailable: Boolean) {
        formRepository.setForm(getForm().copy(isAvailableForSale = isAvailable))
    }

    private fun getForm() = formRepository.getCurrentForm()
}