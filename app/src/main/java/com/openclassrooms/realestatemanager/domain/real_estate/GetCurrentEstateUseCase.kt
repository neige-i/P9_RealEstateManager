package com.openclassrooms.realestatemanager.domain.real_estate

import com.openclassrooms.realestatemanager.data.real_estate.CurrentEstateRepository
import com.openclassrooms.realestatemanager.data.real_estate.RealEstateEntity
import com.openclassrooms.realestatemanager.data.real_estate.RealEstateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCurrentEstateUseCase @Inject constructor(
    private val currentEstateRepository: CurrentEstateRepository,
    private val realEstateRepository: RealEstateRepository,
) {

    operator fun invoke(): Flow<RealEstateResult> = currentEstateRepository.getIdOrNull()
        .flatMapLatest { estateId ->
            if (estateId == null) {
                flowOf(RealEstateResult.Idle)
            } else {
                realEstateRepository.getEstate(estateId)
                    .map { realEstate: RealEstateEntity? ->
                        if (realEstate != null) {
                            RealEstateResult.Success(realEstate)
                        } else {
                            RealEstateResult.Failure(estateId)
                        }
                    }
            }
        }
}