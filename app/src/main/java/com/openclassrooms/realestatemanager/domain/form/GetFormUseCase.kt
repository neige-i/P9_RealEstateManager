package com.openclassrooms.realestatemanager.domain.form

import androidx.lifecycle.LiveData
import com.openclassrooms.realestatemanager.data.form.CurrentPictureEntity
import com.openclassrooms.realestatemanager.data.form.CurrentPictureRepository
import com.openclassrooms.realestatemanager.data.form.FormEntity
import com.openclassrooms.realestatemanager.data.form.FormRepository
import javax.inject.Inject

class GetFormUseCase @Inject constructor(
    private val formRepository: FormRepository,
    private val currentPictureRepository: CurrentPictureRepository,
) {

    fun getForm(): LiveData<FormEntity> = formRepository.getFormLiveData()

    fun getCurrentState(): FormEntity = formRepository.getNonNullForm()

    fun getType(): FormType =
        if (FormRepository.DEFAULT_FORM == formRepository.getInitialState()) {
            FormType.ADD
        } else {
            FormType.EDIT
        }

    fun isModified(): Boolean = formRepository.getNonNullForm() != formRepository.getInitialState()

    fun getCurrentPicture(): LiveData<CurrentPictureEntity?> =
        currentPictureRepository.getCurrentPictureLiveData()

    enum class FormType {
        ADD,
        EDIT,
    }
}