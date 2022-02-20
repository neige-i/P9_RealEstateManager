package com.openclassrooms.realestatemanager.data.form

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentPictureRepository @Inject constructor() {

    private val currentPictureMutableLiveData = MutableLiveData<CurrentPictureEntity?>()
    private var currentPicture: CurrentPictureEntity? = null

    fun getCurrentPictureLiveData(): LiveData<CurrentPictureEntity?> = currentPictureMutableLiveData

    fun getNonNullCurrentPicture(): CurrentPictureEntity = currentPicture
        ?: throw IllegalStateException("Picture is not initialized!")

    fun getCurrentPicture(): CurrentPictureEntity? = currentPicture

    fun setCurrentPicture(currentPicture: CurrentPictureEntity?) {
        this.currentPicture = currentPicture
        currentPictureMutableLiveData.value = currentPicture
    }
}