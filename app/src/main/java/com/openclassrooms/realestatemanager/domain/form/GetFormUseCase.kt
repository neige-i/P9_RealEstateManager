package com.openclassrooms.realestatemanager.domain.form

import com.openclassrooms.realestatemanager.data.form.FormEntity
import com.openclassrooms.realestatemanager.data.form.FormRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFormUseCase @Inject constructor(
    private val formRepository: FormRepository,
) {

    fun getFormFlow(): Flow<FormEntity> = formRepository.getFormFlow()

    fun getFormInfo(): FormInfo {
        val initialState = formRepository.getInitialState()
        val currentState = formRepository.getForm()

        return FormInfo(
            formType = if (FormRepository.DEFAULT_FORM == initialState) {
                FormType.ADD_ESTATE
            } else {
                FormType.EDIT_ESTATE
            },
            isModified = currentState != initialState,
            estateType = currentState.type
        )
    }
}