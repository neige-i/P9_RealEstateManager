package com.openclassrooms.realestatemanager.ui.form.picker_dialog

import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.domain.form.SetFormRequestUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PicturePickerViewModel @Inject constructor(
    private val setFormRequestUseCase: SetFormRequestUseCase,
) : ViewModel() {

    fun onDialogItemClicked(position: Int) {
        setFormRequestUseCase.pickImage(position)
    }
}