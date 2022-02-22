package com.openclassrooms.realestatemanager.data.form

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActionRepository @Inject constructor() {

    private val imagePickerMutableLiveData = MutableLiveData<ImagePicker?>()

    fun getImagePickerLiveData(): LiveData<ImagePicker?> = imagePickerMutableLiveData

    fun setImagePicker(imagePicker: ImagePicker?) {
        imagePickerMutableLiveData.value = imagePicker
    }
}