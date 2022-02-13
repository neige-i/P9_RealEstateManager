package com.openclassrooms.realestatemanager.domain.displayed_picture

import com.openclassrooms.realestatemanager.data.form.DisplayedPictureRepository
import com.openclassrooms.realestatemanager.data.form.FormRepository
import javax.inject.Inject

class SavePictureUseCase @Inject constructor(
    private val displayedPictureRepository: DisplayedPictureRepository,
    private val formRepository: FormRepository,
) {

    operator fun invoke() {
        val pictureUri = displayedPictureRepository.get().uri
        val pictureList = formRepository.getCurrentForm().pictureUriList.toMutableList()
        val positionToUpdate = formRepository.getPositionOfPictureToUpdate()

        if (positionToUpdate == pictureList.size) {
            pictureList.add(pictureUri)
        } else {
            pictureList[positionToUpdate] = pictureUri
        }

        formRepository.setForm(formRepository.getCurrentForm().copy(pictureUriList = pictureList))
    }
}