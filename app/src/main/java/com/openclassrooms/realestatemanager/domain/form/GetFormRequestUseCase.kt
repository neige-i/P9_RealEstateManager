package com.openclassrooms.realestatemanager.domain.form

import androidx.lifecycle.LiveData
import com.openclassrooms.realestatemanager.data.form.ActionRepository
import com.openclassrooms.realestatemanager.data.form.ImagePicker
import javax.inject.Inject

class GetFormRequestUseCase @Inject constructor(
    private val actionRepository: ActionRepository,
) {

    fun getExit(): LiveData<Boolean> = actionRepository.getExitLiveData()

    fun getImagePicker(): LiveData<ImagePicker?> = actionRepository.getImagePickerLiveData()
}