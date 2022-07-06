package com.openclassrooms.realestatemanager.domain.real_estate

import com.openclassrooms.realestatemanager.data.form.FormEntity
import com.openclassrooms.realestatemanager.data.form.FormRepository
import com.openclassrooms.realestatemanager.data.real_estate.RealEstateRepository
import com.openclassrooms.realestatemanager.data.room.EstateEntity
import com.openclassrooms.realestatemanager.data.room.EstatePoiCrossRef
import com.openclassrooms.realestatemanager.data.room.PhotoEntity
import javax.inject.Inject

class SaveRealEstateUseCase @Inject constructor(
    private val formRepository: FormRepository,
    private val realEstateRepository: RealEstateRepository,
) {

    suspend operator fun invoke() {
        val form = formRepository.getForm()

        val realEstateToSave = EstateEntity(
            estateId = form.id,
            type = form.estateType!!,
            price = form.price.toDoubleOrNull(),
            area = form.area.toIntOrNull(),
            totalRoomCount = form.totalRoomCount,
            bathroomCount = form.bathroomCount,
            bedroomCount = form.bedroomCount,
            description = form.description,
            streetName = form.streetName,
            additionalAddressInfo = form.additionalAddressInfo,
            city = form.city,
            state = form.state,
            zipcode = form.zipcode,
            country = form.country,
            agentInChargeId = form.agent?.agentId,
            marketEntryDate = form.marketEntryDate!!,
            saleDate = form.saleDate,
        )

        if (form.id == 0L) {
            addData(newEstate = realEstateToSave, form = form)
        } else {
            editData(editedEstate = realEstateToSave, form = form)
        }
    }

    private suspend fun addData(newEstate: EstateEntity, form: FormEntity) {
        val newlyCreatedEstateId = realEstateRepository.addEstate(newEstate)

        form.pictureList.forEach {
            realEstateRepository.addPhoto(
                PhotoEntity(
                    estateId = newlyCreatedEstateId,
                    uri = it.uri.toString(),
                    description = it.description
                )
            )
        }
        form.pointsOfInterests.forEach { poi ->
            realEstateRepository.addEstatePoiRef(
                EstatePoiCrossRef(
                    estateId = newlyCreatedEstateId,
                    poiValue = poi,
                )
            )
        }
    }

    private suspend fun editData(editedEstate: EstateEntity, form: FormEntity) {
        realEstateRepository.setEstate(editedEstate)
    }
}