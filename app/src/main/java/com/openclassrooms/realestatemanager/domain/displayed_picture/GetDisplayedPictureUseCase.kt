package com.openclassrooms.realestatemanager.domain.displayed_picture

import androidx.lifecycle.LiveData
import com.openclassrooms.realestatemanager.data.form.DisplayedPictureEntity
import com.openclassrooms.realestatemanager.data.form.DisplayedPictureRepository
import javax.inject.Inject

class GetDisplayedPictureUseCase @Inject constructor(
    private val displayedPictureRepository: DisplayedPictureRepository,
) {

    operator fun invoke(): LiveData<DisplayedPictureEntity?> =
        displayedPictureRepository.getLiveData()
}