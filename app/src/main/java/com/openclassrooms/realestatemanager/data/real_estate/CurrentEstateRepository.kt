package com.openclassrooms.realestatemanager.data.real_estate

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentEstateRepository @Inject constructor() {

    private val estateIdChannel = MutableStateFlow<Long?>(null)

    fun getCurrentEstateId(): Flow<Long?> = estateIdChannel

    fun setCurrentEstateId(estateId: Long) {
        estateIdChannel.tryEmit(estateId)
    }
}