package com.openclassrooms.realestatemanager.domain.real_estate

import android.content.Context
import com.openclassrooms.realestatemanager.data.PointOfInterest
import com.openclassrooms.realestatemanager.data.RealEstateType
import com.openclassrooms.realestatemanager.data.agent.AgentRepository
import com.openclassrooms.realestatemanager.data.form.FormRepository
import com.openclassrooms.realestatemanager.data.real_estate.RealEstateEntity
import com.openclassrooms.realestatemanager.data.real_estate.RealEstateRepository
import com.openclassrooms.realestatemanager.domain.form.FormInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SaveRealEstateUseCase @Inject constructor(
    private val formRepository: FormRepository,
    private val realEstateRepository: RealEstateRepository,
    private val agentRepository: AgentRepository,
    @ApplicationContext private val applicationContext: Context,
) {

    suspend operator fun invoke(formType: FormInfo.FormType) {
        val form = formRepository.getForm()

        val realEstateToSave = RealEstateEntity(
            id = form.id,
            type = RealEstateType.fromLocaleString(form.type, applicationContext).name,
            price = form.price.toDoubleOrNull(),
            area = form.area.toIntOrNull(),
            totalRoomCount = form.totalRoomCount,
            bathroomCount = form.bathroomCount,
            bedroomCount = form.bedroomCount,
            description = form.description,
            pictureList = form.pictureList.associate { it.uri.toString() to it.description },
            streetName = form.streetName,
            additionalAddressInfo = form.additionalAddressInfo,
            city = form.city,
            state = form.state,
            zipcode = form.zipcode,
            country = form.country,
            pointsOfInterests = form.pointsOfInterests.map { PointOfInterest.fromLabelId(it).name },
            agentId = agentRepository.getAgentByName(form.agentName)?.id,
            marketEntryDate = form.marketEntryDate,
            saleDate = form.saleDate.ifEmpty { null },
        )

        when (formType) {
            FormInfo.FormType.ADD -> realEstateRepository.createRealEstate(realEstateToSave)
            FormInfo.FormType.EDIT -> realEstateRepository.updateRealEstate(realEstateToSave)
        }
    }
}