package com.openclassrooms.realestatemanager.domain.real_estate

import com.openclassrooms.realestatemanager.data.real_estate.RealEstateEntity

sealed class RealEstateResult {

    object Idle : RealEstateResult()

    data class Success(
        val realEstate: RealEstateEntity,
    ) : RealEstateResult()

    data class Failure(
        val estateId: Long,
    ) : RealEstateResult()
}
