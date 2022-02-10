package com.openclassrooms.realestatemanager.ui.add_edit.pages

import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.domain.EditFormUseCase
import com.openclassrooms.realestatemanager.domain.GetFormInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailInfoViewModel @Inject constructor(
    getFormInfoUseCase: GetFormInfoUseCase,
    private val editFormUseCase: EditFormUseCase,
) : ViewModel() {

    val viewStateLiveData = Transformations.map(getFormInfoUseCase()) {
        DetailViewState(
            description = it.description
        )
    }

    fun onDescriptionChanged(description: String?) {
        editFormUseCase.updateDescription(description ?: "")
    }
}