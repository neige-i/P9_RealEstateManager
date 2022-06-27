package com.openclassrooms.realestatemanager.domain.form

import android.net.Uri
import com.openclassrooms.realestatemanager.data.PointOfInterest
import com.openclassrooms.realestatemanager.data.RealEstateType
import com.openclassrooms.realestatemanager.data.current_photo.CurrentPhotoEntity
import com.openclassrooms.realestatemanager.data.current_photo.CurrentPhotoRepository
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
    private val currentPhotoRepository: CurrentPhotoRepository,
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
                FormEntity.PhotoEntity(uri = Uri.parse(it.uri), description = it.description)
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

    fun removePictureAt(position: Int) {
        modifyPictureList {
            removeAt(position)
        }
    }

    fun resetPictureError() {
        formRepository.setForm(formRepository.getForm().copy(pictureListError = null))
    }

    // CURRENT PICTURE

    fun initPhotoToEdit(index: Int, photo: FormEntity.PhotoEntity) {
        currentPhotoRepository.setPhotoIndex(index)
        currentPhotoRepository.initPhoto(
            CurrentPhotoEntity(
                uri = photo.uri,
                isUriErrorVisible = false,
                description = photo.description,
                descriptionError = null,
            )
        )
    }

    fun initPhotoToAdd() {
        currentPhotoRepository.setPhotoIndex(CurrentPhotoRepository.ADD_PHOTO)
        currentPhotoRepository.initPhoto(CurrentPhotoRepository.DEFAULT_PHOTO)
    }

    suspend fun savePicture() {
        val currentPhoto = currentPhotoRepository.getCurrentPhotoFlow().first()
        val photoIndex = currentPhotoRepository.getPhotoIndexFlow().first()

        val photoToSave = FormEntity.PhotoEntity(
            uri = currentPhoto.uri!!,
            description = currentPhoto.description,
        )

        modifyPictureList {
            if (photoIndex == CurrentPhotoRepository.ADD_PHOTO) {
                add(photoToSave)
            } else {
                set(photoIndex, photoToSave)
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

    private fun modifyPictureList(modification: MutableList<FormEntity.PhotoEntity>.() -> Unit) {
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