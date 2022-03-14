package com.openclassrooms.realestatemanager.domain.real_estate

import com.openclassrooms.realestatemanager.data.real_estate.CurrentEstateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import javax.inject.Inject

class GetCurrentEstateIdUseCase @Inject constructor(
    private val currentEstateRepository: CurrentEstateRepository,
) {

    operator fun invoke(): Flow<Long> = currentEstateRepository.getCurrentEstateId().filterNotNull()
}