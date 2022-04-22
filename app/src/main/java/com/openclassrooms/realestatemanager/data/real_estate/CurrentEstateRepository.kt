package com.openclassrooms.realestatemanager.data.real_estate

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterNotNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentEstateRepository @Inject constructor() {

    private val estateIdMutableSharedFlow = MutableSharedFlow<Long?>(replay = 1).apply {
        tryEmit(null)
    }

    fun getId(): Flow<Long> = estateIdMutableSharedFlow.filterNotNull()

    fun getIdOrNull(): Flow<Long?> = estateIdMutableSharedFlow

    fun setId(estateId: Long?) {
        estateIdMutableSharedFlow.tryEmit(estateId)
    }
}