package com.openclassrooms.realestatemanager.ui.form.picker_dialog

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.openclassrooms.realestatemanager.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ImagePickerDialog(
    private val imagePickerLifecycleObserver: ImagePickerLifecycleObserver,
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val imagePickerDialogItems = arrayOf(
            getString(R.string.image_picker_dialog_gallery_item),
            getString(R.string.image_picker_dialog_camera_item)
        )

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.image_picker_dialog_title)
            .setItems(imagePickerDialogItems) { _, which ->
                when (which) {
                    0 -> imagePickerLifecycleObserver.openGallery()
                    1 -> imagePickerLifecycleObserver.openCamera()
                }
            }
            .create()
    }
}