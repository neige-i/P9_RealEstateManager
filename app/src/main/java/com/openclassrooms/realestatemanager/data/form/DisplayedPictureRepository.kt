package com.openclassrooms.realestatemanager.data.form

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DisplayedPictureRepository @Inject constructor() {

    private val displayedPictureMutableLiveData = MutableLiveData<DisplayedPictureEntity?>()
    private var displayedPicture: DisplayedPictureEntity? = null

    fun getLiveData(): LiveData<DisplayedPictureEntity?> = displayedPictureMutableLiveData

    fun get(): DisplayedPictureEntity =
        displayedPicture ?: throw NullPointerException("Picture is not initialized!")

    fun isInitialized(): Boolean = displayedPicture != null

    fun set(displayedPicture: DisplayedPictureEntity?) {
        this.displayedPicture = displayedPicture
        displayedPictureMutableLiveData.value = displayedPicture
    }
}