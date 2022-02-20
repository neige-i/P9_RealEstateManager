package com.openclassrooms.realestatemanager.data.form

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActionRepository @Inject constructor() {

    private val exitMutableLiveData = MutableLiveData<Boolean>()
    private val imagePickerMutableLiveData = MutableLiveData<ImagePicker?>()

    fun getExitLiveData(): LiveData<Boolean> = exitMutableLiveData

    fun setExit(exit: Boolean) {
        exitMutableLiveData.value = exit
    }

    fun getImagePickerLiveData(): LiveData<ImagePicker?> = imagePickerMutableLiveData

    fun setImagePicker(imagePicker: ImagePicker?) {
        imagePickerMutableLiveData.value = imagePicker
    }
}