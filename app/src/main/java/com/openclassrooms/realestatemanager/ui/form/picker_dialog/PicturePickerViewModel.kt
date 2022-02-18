package com.openclassrooms.realestatemanager.ui.form.picker_dialog

import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.domain.form.SetFormUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PicturePickerViewModel @Inject constructor(
    private val setFormUseCase: SetFormUseCase,
) : ViewModel() {

    fun onDialogItemClicked(position: Int) {
        setFormUseCase.setPicturePicker(position)
    }
}