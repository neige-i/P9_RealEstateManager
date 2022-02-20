package com.openclassrooms.realestatemanager.domain.form

import android.net.Uri
import androidx.annotation.StringRes
import com.openclassrooms.realestatemanager.data.form.CurrentPictureEntity
import com.openclassrooms.realestatemanager.data.form.CurrentPictureRepository
import com.openclassrooms.realestatemanager.data.form.FormEntity
import com.openclassrooms.realestatemanager.data.form.FormRepository
import javax.inject.Inject

class SetFormUseCase @Inject constructor(
    private val formRepository: FormRepository,
    private val currentPictureRepository: CurrentPictureRepository,
) {

    /*
        For EditText related fields, set the new value only if it is different from the current one.
        Otherwise, the cursor will automatically be placed at the end of the text.
     */

    fun initAddForm() {
        formRepository.initForm(FormRepository.DEFAULT_FORM)

        // Reset errors when starting the form
        val existingForm = formRepository.getForm()?.copy(
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

        formRepository.setForm(existingForm ?: FormRepository.DEFAULT_FORM)
    }

    fun reset() {
        formRepository.resetForm()
    }

    // MAIN INFO

    fun updateType(type: String) {
        formRepository.setForm(getForm().copy(type = type))
    }

    fun updatePrice(price: String, cursorPosition: Int) {
        if (price != getForm().price) {
            formRepository.setForm(getForm().copy(price = price, priceCursor = cursorPosition))
        }
    }

    fun updateArea(area: String, cursorPosition: Int) {
        if (area != getForm().area) {
            formRepository.setForm(getForm().copy(area = area, areaCursor = cursorPosition))
        }
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

    // DETAIL INFO

    fun updateDescription(description: String, cursorPosition: Int) {
        if (description != getForm().description) {
            formRepository.setForm(
                getForm().copy(
                    description = description,
                    descriptionCursor = cursorPosition
                )
            )
        }
    }

    fun setPicturePosition(position: Int) {
        formRepository.setCurrentPicturePosition(position)
    }

    fun removePictureAt(position: Int) {
        val pictureList = getForm().pictureList.toMutableList()

        pictureList.removeAt(position)
        formRepository.setForm(getForm().copy(pictureList = pictureList))
    }

    fun resetPictureError() {
        formRepository.setForm(getForm().copy(pictureListError = null))
    }

    // CURRENT PICTURE

    fun setPictureUri(uri: Uri) {
        val picture = currentPictureRepository.getCurrentPicture()

        if (picture != null) {
            currentPictureRepository.setCurrentPicture(picture.copy(uri = uri))
        } else {
            initPicture(uri, "")
        }
    }

    fun initPicture(pictureUri: Uri, pictureDescription: String) {
        currentPictureRepository.setCurrentPicture(
            CurrentPictureEntity(
                uri = pictureUri,
                description = pictureDescription,
                descriptionError = null,
                descriptionCursor = 0,
            )
        )
    }

    fun updatePictureDescription(description: String, cursorPosition: Int) {
        val picture = getPicture()

        if (description != picture.description) {
            currentPictureRepository.setCurrentPicture(
                picture.copy(
                    description = description,
                    descriptionCursor = cursorPosition
                )
            )
        }
    }

    fun resetPicture() {
        currentPictureRepository.setCurrentPicture(null)
    }

    fun savePicture() {
        val picture = FormEntity.PictureEntity(
            uri = getPicture().uri,
            description = getPicture().description,
        )
        val pictureList = getForm().pictureList.toMutableList()
        val picturePosition = formRepository.getCurrentPicturePosition()

        if (picturePosition == pictureList.size) {
            pictureList.add(picture)
        } else {
            pictureList[picturePosition] = picture
        }

        formRepository.setForm(getForm().copy(pictureList = pictureList))
    }

    // ADDRESS

    fun updateStreetName(streetNameHouseNumber: String, cursorPosition: Int) {
        if (streetNameHouseNumber != getForm().streetName) {
            formRepository.setForm(
                getForm().copy(
                    streetName = streetNameHouseNumber,
                    streetNameCursor = cursorPosition
                )
            )
        }
    }

    fun updateAdditionalAddressInfo(additionalAddressInfo: String, cursorPosition: Int) {
        if (additionalAddressInfo != getForm().additionalAddressInfo) {
            formRepository.setForm(
                getForm().copy(
                    additionalAddressInfo = additionalAddressInfo,
                    additionalAddressInfoCursor = cursorPosition
                )
            )
        }
    }

    fun updateCity(city: String, cursorPosition: Int) {
        if (city != getForm().city) {
            formRepository.setForm(getForm().copy(city = city, cityCursor = cursorPosition))
        }
    }

    fun updateState(state: String, cursorPosition: Int) {
        if (state != getForm().state) {
            formRepository.setForm(
                getForm().copy(
                    state = state.uppercase(),
                    stateCursor = cursorPosition
                )
            )
        }
    }

    fun updateZipcode(zipcode: String, cursorPosition: Int) {
        if (zipcode != getForm().zipcode) {
            formRepository.setForm(
                getForm().copy(
                    zipcode = zipcode,
                    zipcodeCursor = cursorPosition
                )
            )
        }
    }

    fun updateCountry(country: String, cursorPosition: Int) {
        if (country != getForm().country) {
            formRepository.setForm(
                getForm().copy(
                    country = country,
                    countryCursor = cursorPosition
                )
            )
        }
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

    // SALE STATUS

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

    private fun getForm() = formRepository.getNonNullForm()

    private fun getPicture() = currentPictureRepository.getNonNullCurrentPicture()
}