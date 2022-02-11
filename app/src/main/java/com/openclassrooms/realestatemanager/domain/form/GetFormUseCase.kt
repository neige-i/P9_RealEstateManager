package com.openclassrooms.realestatemanager.domain.form

import androidx.lifecycle.LiveData
import com.openclassrooms.realestatemanager.data.form.FormEntity
import com.openclassrooms.realestatemanager.data.form.FormRepository
import javax.inject.Inject

class GetFormUseCase @Inject constructor(
    private val formRepository: FormRepository,
) {

    fun getUpdates(): LiveData<FormEntity> = formRepository.getFormLiveData()

    fun getCurrent(): FormEntity = formRepository.getCurrentForm()

    fun getType(): FormRepository.FormType = formRepository.getFormType()

    fun isModified(): Boolean = formRepository.containsModifications()
}