package com.openclassrooms.realestatemanager.domain.real_estate

import android.content.Context
import com.openclassrooms.realestatemanager.data.PointOfInterest
import com.openclassrooms.realestatemanager.data.RealEstateType
import com.openclassrooms.realestatemanager.data.form.FormEntity
import com.openclassrooms.realestatemanager.data.form.FormRepository
import com.openclassrooms.realestatemanager.data.real_estate.RealEstateRepository
import com.openclassrooms.realestatemanager.data.room.EstateAgentCrossRef
import com.openclassrooms.realestatemanager.data.room.EstateEntity
import com.openclassrooms.realestatemanager.data.room.EstatePoiCrossRef
import com.openclassrooms.realestatemanager.data.room.PhotoEntity
import com.openclassrooms.realestatemanager.domain.form.FormInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SaveRealEstateUseCase @Inject constructor(
    private val formRepository: FormRepository,
    private val realEstateRepository: RealEstateRepository,
    @ApplicationContext private val applicationContext: Context,
) {

    suspend operator fun invoke(formType: FormInfo.FormType) {
        val form = formRepository.getForm()

        val realEstateToSave = EstateEntity(
            realEstateId = form.id,
            type = RealEstateType.fromLocaleString(form.type, applicationContext).name,
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
            marketEntryDate = form.marketEntryDate,
            saleDate = form.saleDate.ifEmpty { null },
        )

        when (formType) {
            FormInfo.FormType.ADD -> addData(newEstate = realEstateToSave, form = form)
            FormInfo.FormType.EDIT -> editData(editedEstate = realEstateToSave, form = form)
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
        form.pointsOfInterests.forEach {
            realEstateRepository.addEstatePoiRef(
                EstatePoiCrossRef(
                    realEstateId = newlyCreatedEstateId,
                    poiValue = PointOfInterest.fromLabelId(it).name
                )
            )
        }
        if (form.agentName.isNotEmpty()) {
            realEstateRepository.addEstateAgentRef(
                EstateAgentCrossRef(
                    realEstateId = newlyCreatedEstateId,
                    username = form.agentName
                )
            )
        }
    }

    private suspend fun editData(editedEstate: EstateEntity, form: FormEntity) {
        realEstateRepository.setEstate(editedEstate)

        val estateAgentCrossRef = EstateAgentCrossRef(
            realEstateId = editedEstate.realEstateId,
            username = form.agentName
        )

        if (form.agentName.isNotEmpty()) {
            if (realEstateRepository.isEstateTakenCareByAgent(editedEstate.realEstateId)) {
                realEstateRepository.setEstateAgentRef(estateAgentCrossRef)
            } else {
                realEstateRepository.addEstateAgentRef(estateAgentCrossRef)
            }
        }
    }
}