package com.openclassrooms.realestatemanager.data.form

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActionRepository @Inject constructor() {

    private val imagePickerMutableSharedFlow = MutableSharedFlow<ImagePicker?>(replay = 1)

    fun getImagePickerFlow(): Flow<ImagePicker?> = imagePickerMutableSharedFlow

    fun requestImagePicking(imagePicker: ImagePicker) {
        imagePickerMutableSharedFlow.tryEmit(imagePicker)
    }

    fun flushImagePickerFlow() {
        imagePickerMutableSharedFlow.tryEmit(null)
    }
}