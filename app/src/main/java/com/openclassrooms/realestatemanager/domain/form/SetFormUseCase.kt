package com.openclassrooms.realestatemanager.domain.form

import android.net.Uri
import com.openclassrooms.realestatemanager.data.PointOfInterest
import com.openclassrooms.realestatemanager.data.RealEstateType
import com.openclassrooms.realestatemanager.data.form.CurrentPictureEntity
import com.openclassrooms.realestatemanager.data.form.CurrentPictureRepository
import com.openclassrooms.realestatemanager.data.form.FormEntity
import com.openclassrooms.realestatemanager.data.form.FormRepository
import com.openclassrooms.realestatemanager.data.real_estate.RealEstateEntity
import com.openclassrooms.realestatemanager.domain.form.FormType.ADD_ESTATE
import com.openclassrooms.realestatemanager.domain.form.FormType.EDIT_ESTATE
import com.openclassrooms.realestatemanager.domain.real_estate.GetCurrentEstateUseCase
import com.openclassrooms.realestatemanager.domain.real_estate.RealEstateResult
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import javax.inject.Inject

class SetFormUseCase @Inject constructor(
    private val formRepository: FormRepository,
    private val getCurrentEstateUseCase: GetCurrentEstateUseCase,
    private val currentPictureRepository: CurrentPictureRepository,
) {

    suspend fun initForm(formType: FormType) {
        when (formType) {
            ADD_ESTATE -> {
                formRepository.setInitialState(FormRepository.DEFAULT_FORM)
                formRepository.setForm(
                    formRepository.getForm().copy(
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
                )
            }
            EDIT_ESTATE -> {
                val currentEstateResult = getCurrentEstateUseCase.invoke().first()

                if (currentEstateResult is RealEstateResult.Success) {
                    val form = mapEstateToForm(currentEstateResult.realEstate)
                    formRepository.setInitialState(form)
                    formRepository.setForm(form)
                }
            }
        }
    }

    private fun mapEstateToForm(realEstate: RealEstateEntity): FormEntity {
        val price = realEstate.info.price?.toString() ?: ""
        val area = realEstate.info.area?.toString() ?: ""

        return FormEntity(
            id = realEstate.info.estateId,
            estateType = realEstate.info.type,
            typeError = null,
            price = price,
            area = area,
            totalRoomCount = realEstate.info.totalRoomCount,
            bathroomCount = realEstate.info.bathroomCount,
            bedroomCount = realEstate.info.bedroomCount,
            description = realEstate.info.description,
            pictureList = realEstate.photoList.map {
                FormEntity.PictureEntity(uri = Uri.parse(it.uri), description = it.description)
            },
            pictureListError = null,
            streetName = realEstate.info.streetName,
            streetNameError = null,
            additionalAddressInfo = realEstate.info.additionalAddressInfo,
            city = realEstate.info.city,
            cityError = null,
            state = realEstate.info.state,
            stateError = null,
            zipcode = realEstate.info.zipcode,
            zipcodeError = null,
            country = realEstate.info.country,
            countryError = null,
            pointsOfInterests = realEstate.poiList.map { it.poiValue },
            agentName = realEstate.agent?.username.orEmpty(),
            marketEntryDate = realEstate.info.marketEntryDate,
            marketEntryDateError = null,
            saleDate = realEstate.info.saleDate,
            saleDateError = null,
            isAvailableForSale = realEstate.info.saleDate == null,
        )
    }

    fun reset() {
        formRepository.resetAll()
    }

    // MAIN INFO

    fun updateType(estateType: RealEstateType) {
        formRepository.setForm(formRepository.getForm().copy(estateType = estateType))
    }

    fun updatePrice(price: String) {
        formRepository.setForm(
            formRepository.getForm().copy(price = price)
        )
    }

    fun updateArea(area: String) {
        formRepository.setForm(
            formRepository.getForm().copy(area = area)
        )
    }

    fun incTotalRoomCount() {
        formRepository.setForm(
            formRepository.getForm().let { it.copy(totalRoomCount = it.totalRoomCount.inc()) }
        )
    }

    fun decTotalRoomCount() {
        if (areAnyRoomsLeft()) {
            formRepository.setForm(
                formRepository.getForm().let { it.copy(totalRoomCount = it.totalRoomCount.dec()) }
            )
        }
    }

    fun incBathroomCount() {
        if (areAnyRoomsLeft()) {
            formRepository.setForm(
                formRepository.getForm().let { it.copy(bathroomCount = it.bathroomCount.inc()) }
            )
        }
    }

    fun decBathroomCount() {
        val currentForm = formRepository.getForm()

        if (currentForm.bathroomCount > 0) {
            formRepository.setForm(
                currentForm.copy(bathroomCount = currentForm.bathroomCount.dec())
            )
        }
    }

    fun incBedroomCount() {
        if (areAnyRoomsLeft()) {
            formRepository.setForm(
                formRepository.getForm().let { it.copy(bedroomCount = it.bedroomCount.inc()) }
            )
        }
    }

    fun decBedroomCount() {
        val currentForm = formRepository.getForm()

        if (currentForm.bedroomCount > 0) {
            formRepository.setForm(
                currentForm.copy(bedroomCount = currentForm.bedroomCount.dec())
            )
        }
    }

    private fun areAnyRoomsLeft(): Boolean = formRepository.getForm().run {
        totalRoomCount - bathroomCount - bedroomCount > 0
    }

    // DETAIL INFO

    fun updateDescription(description: String) {
        formRepository.setForm(
            formRepository.getForm().copy(description = description)
        )
    }

    fun updatePicturePosition(position: Int) {
        formRepository.setCurrentPicturePosition(position)
    }

    fun removePictureAt(position: Int) {
        modifyPictureList {
            removeAt(position)
        }
    }

    fun resetPictureError() {
        formRepository.setForm(formRepository.getForm().copy(pictureListError = null))
    }

    // CURRENT PICTURE

    fun initPicture(uri: Uri, description: String = "") {
        currentPictureRepository.setPicture(
            CurrentPictureEntity(
                uri = uri,
                description = description,
                descriptionError = null,
            )
        )
    }

    fun updatePictureDescription(description: String) {
        currentPictureRepository.setPicture(
            currentPictureRepository.getCurrentPicture()?.copy(description = description)
        )
    }

    fun resetPicture() {
        currentPictureRepository.setPicture(null)
    }

    fun savePicture() {
        val currentPicture = currentPictureRepository.getCurrentPicture() ?: return
        val picturePosition = formRepository.getCurrentPicturePosition()

        val picture = FormEntity.PictureEntity(
            uri = currentPicture.uri,
            description = currentPicture.description,
        )

        modifyPictureList {
            if (formRepository.getForm().pictureList.size == picturePosition) {
                add(picture)
            } else {
                set(picturePosition, picture)
            }
        }
    }

    // ADDRESS

    fun updateStreetName(streetName: String) {
        formRepository.setForm(
            formRepository.getForm().copy(streetName = streetName)
        )
    }

    fun updateAdditionalAddressInfo(additionalAddressInfo: String) {
        formRepository.setForm(
            formRepository.getForm().copy(additionalAddressInfo = additionalAddressInfo)
        )
    }

    fun updateCity(city: String) {
        formRepository.setForm(
            formRepository.getForm().copy(city = city)
        )
    }

    fun updateState(state: String) {
        formRepository.setForm(
            formRepository.getForm().copy(state = state)
        )
    }

    fun updateZipcode(zipcode: String) {
        formRepository.setForm(
            formRepository.getForm().copy(zipcode = zipcode)
        )
    }

    fun updateCountry(country: String) {
        formRepository.setForm(
            formRepository.getForm().copy(country = country)
        )
    }

    fun updatePoi(poi: PointOfInterest, isChecked: Boolean) {
        val updatedForm = formRepository.getForm().let {
            it.copy(
                pointsOfInterests = applyModificationOn(it.pointsOfInterests) {
                    if (isChecked) {
                        add(poi)
                    } else {
                        remove(poi)
                    }
                }
            )
        }

        formRepository.setForm(updatedForm)
    }

    // SALE STATUS

    fun updateAgent(agentName: String) {
        formRepository.setForm(
            formRepository.getForm().copy(agentName = agentName)
        )
    }

    fun updateMarketEntryDate(marketEntryDate: LocalDate) {
        formRepository.setForm(
            formRepository.getForm().copy(marketEntryDate = marketEntryDate)
        )
    }

    fun updateSaleDate(saleDate: LocalDate) {
        formRepository.setForm(
            formRepository.getForm().copy(saleDate = saleDate)
        )
    }

    fun updateAvailability(isAvailable: Boolean) {
        val currentForm = formRepository.getForm()

        formRepository.setForm(
            currentForm.copy(
                isAvailableForSale = isAvailable,
                saleDate = if (!isAvailable) null else currentForm.saleDate
            )
        )
    }

    private fun modifyPictureList(modification: MutableList<FormEntity.PictureEntity>.() -> Unit) {
        val currentForm = formRepository.getForm()

        formRepository.setForm(
            currentForm.copy(
                pictureList = applyModificationOn(currentForm.pictureList) { modification() }
            )
        )
    }

    private inline fun <T> applyModificationOn(
        list: List<T>,
        modification: MutableList<T>.() -> Unit
    ): MutableList<T> {
        return list.toMutableList().apply { modification(this) }
    }
}