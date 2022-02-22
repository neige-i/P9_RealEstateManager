package com.openclassrooms.realestatemanager.domain.form

import com.openclassrooms.realestatemanager.data.form.ActionRepository
import com.openclassrooms.realestatemanager.data.form.ImagePicker
import javax.inject.Inject

class SetFormRequestUseCase @Inject constructor(
    private val actionRepository: ActionRepository,
) {

    fun pickImage(position: Int?) {
        actionRepository.setImagePicker(position?.let { ImagePicker.values()[it] })
    }
}