package com.openclassrooms.realestatemanager.ui.form.picker_dialog

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.form.FormRepository
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PicturePickerDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val viewModel = ViewModelProvider(this).get(PicturePickerViewModel::class.java)

        val imagePickerDialogItems = FormRepository.PicturePicker.values().map {
            getString(it.labelId)
        }.toTypedArray()

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.image_picker_dialog_title)
            .setItems(imagePickerDialogItems) { _, which -> viewModel.onDialogItemClicked(which) }
            .create()
    }
}