package com.openclassrooms.realestatemanager.domain.form

import android.net.Uri
import androidx.annotation.StringRes
import com.openclassrooms.realestatemanager.data.form.DisplayedPictureEntity
import com.openclassrooms.realestatemanager.data.form.FormEntity
import com.openclassrooms.realestatemanager.data.form.FormRepository
import javax.inject.Inject

class SetFormUseCase @Inject constructor(
    private val formRepository: FormRepository,
) {

    /*
        For EditText related fields, set the new value only if it is different from the current one.
        Otherwise, the cursor will automatically be placed at the end of the text.
     */

    fun initAddForm() {
        formRepository.initForm()
    }

    fun reset() {
        formRepository.resetForm()
    }

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
        formRepository.setPositionOfPictureToUpdate(position)
    }

    fun initPicture(pictureUri: Uri, pictureDescription: String) {
        formRepository.setDisplayedPicture(
            DisplayedPictureEntity(
                uri = pictureUri,
                description = pictureDescription,
                descriptionError = null,
                descriptionCursor = 0,
            )
        )
    }

    fun updatePictureDescription(description: String, cursorPosition: Int) {
        val picture = formRepository.getCurrentDisplayedPicture()
        if (description != picture.description) {
            formRepository.setDisplayedPicture(
                picture.copy(
                    description = description,
                    descriptionCursor = cursorPosition
                )
            )
        }
    }

    fun resetPicture() {
        formRepository.setDisplayedPicture(null)
    }

    fun setPictureUri(uri: Uri) {
        if (formRepository.isPictureInitialized()) {
            val picture = formRepository.getCurrentDisplayedPicture()
            formRepository.setDisplayedPicture(picture.copy(uri = uri))
        } else {
            initPicture(uri, "")
        }
    }

    fun savePicture() {
        val picture = FormEntity.PictureEntity(
            uri = formRepository.getCurrentDisplayedPicture().uri,
            description = formRepository.getCurrentDisplayedPicture().description,
        )
        val pictureList = formRepository.getCurrentForm().pictureList.toMutableList()
        val positionToUpdate = formRepository.getPositionOfPictureToUpdate()

        if (positionToUpdate == pictureList.size) {
            pictureList.add(picture)
        } else {
            pictureList[positionToUpdate] = picture
        }

        formRepository.setForm(formRepository.getCurrentForm().copy(pictureList = pictureList))
    }

    fun removePhoto(position: Int) {
        val pictureList = getForm().pictureList.toMutableList()
        pictureList.removeAt(position)
        formRepository.setForm(getForm().copy(pictureList = pictureList))
    }

    fun resetPictureError() {
        formRepository.setForm(getForm().copy(pictureListError = null))
    }

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

    fun setExitRequest(exit: Boolean) {
        formRepository.setExitRequest(exit)
    }

    fun setPicturePicker(position: Int?) {
        val picturePicker = if (position != null) {
            FormRepository.PicturePicker.values()[position]
        } else {
            null
        }
        formRepository.setPicturePicker(picturePicker)
    }

    private fun getForm() = formRepository.getCurrentForm()
}