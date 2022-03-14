package com.openclassrooms.realestatemanager.data.form

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentPictureRepository @Inject constructor() {

    private val currentPictureMutableStateFlow = MutableStateFlow<CurrentPictureEntity?>(null)

    fun getCurrentPictureFlow(): Flow<CurrentPictureEntity?> = currentPictureMutableStateFlow

    fun getCurrentPicture(): CurrentPictureEntity? = currentPictureMutableStateFlow.value

    fun initPicture(uri: Uri, description: String) {
        currentPictureMutableStateFlow.value = CurrentPictureEntity(
            uri = uri,
            description = description,
            descriptionError = null,
            descriptionCursor = 0
        )
    }

    fun setUri(uri: Uri) {
        currentPictureMutableStateFlow.update {
            it?.copy(uri = uri)
        }
    }

    fun setDescription(description: String, cursorPosition: Int) {
        currentPictureMutableStateFlow.update {
            it?.copy(description = description, descriptionCursor = cursorPosition)
        }
    }

    fun setDescriptionError(descriptionError: String?) {
        currentPictureMutableStateFlow.update {
            it?.copy(descriptionError = descriptionError)
        }
    }

    fun reset() {
        currentPictureMutableStateFlow.value = null
    }
}