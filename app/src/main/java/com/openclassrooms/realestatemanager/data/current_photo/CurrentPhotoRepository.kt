package com.openclassrooms.realestatemanager.data.current_photo

import android.net.Uri
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentPhotoRepository @Inject constructor() {

    companion object {
        const val ADD_PHOTO = -1

        val DEFAULT_PHOTO = CurrentPhotoEntity(
            uri = null,
            isUriErrorVisible = false,
            description = "",
            descriptionError = null,
        )
    }

    private val photoIndexMutableSharedFlow = MutableSharedFlow<Int?>(replay = 1)
    private val currentPhotoMutableStateFlow = MutableStateFlow(DEFAULT_PHOTO)

    fun getPhotoIndexFlow(): Flow<Int> = photoIndexMutableSharedFlow.filterNotNull()

    fun setPhotoIndex(index: Int) {
        photoIndexMutableSharedFlow.tryEmit(index)
    }

    fun resetPhotoIndex() {
        photoIndexMutableSharedFlow.tryEmit(null)
    }

    fun getCurrentPhotoFlow(): Flow<CurrentPhotoEntity> = currentPhotoMutableStateFlow

    fun initPhoto(currentPhoto: CurrentPhotoEntity) {
        currentPhotoMutableStateFlow.value = currentPhoto
    }

    fun setUri(photoUri: Uri) {
        currentPhotoMutableStateFlow.update { it.copy(uri = photoUri) }
    }

    fun setDescription(photoDescription: String) {
        currentPhotoMutableStateFlow.update { it.copy(description = photoDescription) }
    }

    fun setErrors(descriptionError: String?, isUriSelected: Boolean) {
        currentPhotoMutableStateFlow.update {
            it.copy(
                isUriErrorVisible = !isUriSelected,
                descriptionError = descriptionError,
            )
        }
    }
}