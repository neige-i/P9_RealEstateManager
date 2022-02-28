package com.openclassrooms.realestatemanager.ui.form.picker_dialog

import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.data.form.ActionRepository
import com.openclassrooms.realestatemanager.data.form.ImagePicker
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PicturePickerViewModel @Inject constructor(
    private val actionRepository: ActionRepository,
) : ViewModel() {

    fun onDialogItemClicked(position: Int) {
        actionRepository.requestImagePicking(ImagePicker.values()[position])
    }
}