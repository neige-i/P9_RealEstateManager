package com.openclassrooms.realestatemanager.domain.displayed_picture

import android.net.Uri
import com.openclassrooms.realestatemanager.data.form.DisplayedPictureEntity
import com.openclassrooms.realestatemanager.data.form.DisplayedPictureRepository
import javax.inject.Inject

class SetDisplayedPictureUseCase @Inject constructor(
    private val displayedPictureRepository: DisplayedPictureRepository,
) {

    fun init(pictureUri: Uri, pictureDescription: String) {
        displayedPictureRepository.set(
            DisplayedPictureEntity(
                uri = pictureUri,
                description = pictureDescription,
                descriptionError = null,
                descriptionCursor = 0,
            )
        )
    }

    fun updateDescription(description: String, cursorPosition: Int) {
        val picture = displayedPictureRepository.get()
        if (description != picture.description) {
            displayedPictureRepository.set(
                picture.copy(
                    description = description,
                    descriptionCursor = cursorPosition
                )
            )
        }
    }

    fun reset() {
        displayedPictureRepository.set(null)
    }

    fun setUri(uri: Uri) {
        if (displayedPictureRepository.isInitialized()) {
            val picture = displayedPictureRepository.get()
            displayedPictureRepository.set(picture.copy(uri = uri))
        } else {
            init(uri, "")
        }
    }
}