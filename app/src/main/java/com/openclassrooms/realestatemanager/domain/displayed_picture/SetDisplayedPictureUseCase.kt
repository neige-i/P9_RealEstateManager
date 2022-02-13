package com.openclassrooms.realestatemanager.domain.displayed_picture

import android.net.Uri
import com.openclassrooms.realestatemanager.data.form.DisplayedPictureEntity
import com.openclassrooms.realestatemanager.data.form.DisplayedPictureRepository
import javax.inject.Inject

class SetDisplayedPictureUseCase @Inject constructor(
    private val displayedPictureRepository: DisplayedPictureRepository,
) {

    fun init(pictureUri: Uri, pictureDescription: String = "") {
        displayedPictureRepository.set(DisplayedPictureEntity(
            uri = pictureUri,
            description = pictureDescription,
            descriptionError = null
        ))
    }

    fun updateDescription(description: String) {
        val picture = displayedPictureRepository.get()
        displayedPictureRepository.set(picture.copy(description = description))
    }

    fun reset() {
        displayedPictureRepository.set(null)
    }
}