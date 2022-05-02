package com.openclassrooms.realestatemanager.data.form

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentPictureRepository @Inject constructor() {

    private val pictureMutableStateFlow = MutableStateFlow<CurrentPictureEntity?>(null)

    fun getPictureFlow(): Flow<CurrentPictureEntity> = pictureMutableStateFlow.filterNotNull()

    fun getCurrentPicture(): CurrentPictureEntity? = pictureMutableStateFlow.value

    fun setPicture(currentPictureEntity: CurrentPictureEntity?) {
        pictureMutableStateFlow.value = currentPictureEntity
    }
}