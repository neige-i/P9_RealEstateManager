package com.openclassrooms.realestatemanager.data.form

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentPictureRepository @Inject constructor() {

    private val currentPictureMutableStateFlow = MutableStateFlow<CurrentPictureEntity?>(null)

    fun getCurrentPictureFlow(): Flow<CurrentPictureEntity?> = currentPictureMutableStateFlow

    fun getCurrentPicture(): CurrentPictureEntity? = currentPictureMutableStateFlow.value

    fun setPicture(currentPictureEntity: CurrentPictureEntity?) {
        currentPictureMutableStateFlow.value = currentPictureEntity
    }
}