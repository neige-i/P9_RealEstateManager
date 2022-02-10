package com.openclassrooms.realestatemanager.domain

import androidx.lifecycle.LiveData
import com.openclassrooms.realestatemanager.data.form.FormEntity
import com.openclassrooms.realestatemanager.data.form.FormRepository
import javax.inject.Inject

class GetFormInfoUseCase @Inject constructor(
    private val formRepository: FormRepository,
) {

    operator fun invoke(): LiveData<FormEntity> = formRepository.getForm()
}