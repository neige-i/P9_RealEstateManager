package com.openclassrooms.realestatemanager.data.real_estate

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentEstateRepository @Inject constructor() {

    private val estateIdChannel = MutableSharedFlow<Long>(replay = 1)

    fun getCurrentEstateId(): Flow<Long> = estateIdChannel

    fun setCurrentEstateId(estateId: Long) {
        estateIdChannel.tryEmit(estateId)
    }
}