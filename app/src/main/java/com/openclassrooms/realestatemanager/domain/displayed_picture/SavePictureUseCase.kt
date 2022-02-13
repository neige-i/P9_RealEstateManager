package com.openclassrooms.realestatemanager.domain.displayed_picture

import com.openclassrooms.realestatemanager.data.form.DisplayedPictureRepository
import com.openclassrooms.realestatemanager.data.form.FormEntity
import com.openclassrooms.realestatemanager.data.form.FormRepository
import javax.inject.Inject

class SavePictureUseCase @Inject constructor(
    private val displayedPictureRepository: DisplayedPictureRepository,
    private val formRepository: FormRepository,
) {

    operator fun invoke() {
        val picture = FormEntity.PictureEntity(
            uri = displayedPictureRepository.get().uri,
            description = displayedPictureRepository.get().description,
        )
        val pictureList = formRepository.getCurrentForm().pictureList.toMutableList()
        val positionToUpdate = formRepository.getPositionOfPictureToUpdate()

        if (positionToUpdate == pictureList.size) {
            pictureList.add(picture)
        } else {
            pictureList[positionToUpdate] = picture
        }

        formRepository.setForm(formRepository.getCurrentForm().copy(pictureList = pictureList))
    }
}