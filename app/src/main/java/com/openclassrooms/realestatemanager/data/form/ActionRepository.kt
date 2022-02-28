package com.openclassrooms.realestatemanager.data.form

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActionRepository @Inject constructor() {

    private val imagePickerMutableSharedFlow = MutableSharedFlow<ImagePicker?>(replay = 1)
    private val pictureOpenerChannel = Channel<Boolean>(Channel.CONFLATED)

    fun getImagePickerFlow(): Flow<ImagePicker?> = imagePickerMutableSharedFlow

    fun requestImagePicking(imagePicker: ImagePicker) {
        imagePickerMutableSharedFlow.tryEmit(imagePicker)
    }

    fun flushImagePickerFlow() {
        imagePickerMutableSharedFlow.tryEmit(null)
    }

    fun getPictureOpenerFlow(): Flow<Boolean> = pictureOpenerChannel.receiveAsFlow()

    fun requestPictureOpening() {
        pictureOpenerChannel.trySend(true)
    }
}