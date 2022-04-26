package com.openclassrooms.realestatemanager.ui.form.picker_dialog

import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.data.form.FormRepository
import com.openclassrooms.realestatemanager.data.form.ImagePicker
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PicturePickerViewModel @Inject constructor(
    private val formRepository: FormRepository,
) : ViewModel() {

    fun onDialogItemClicked(position: Int) {
        formRepository.setImagePicker(ImagePicker.values()[position])
    }
}