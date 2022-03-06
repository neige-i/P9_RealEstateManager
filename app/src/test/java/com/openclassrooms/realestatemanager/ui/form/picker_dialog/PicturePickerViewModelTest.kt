package com.openclassrooms.realestatemanager.ui.form.picker_dialog

import com.openclassrooms.realestatemanager.data.form.ActionRepository
import com.openclassrooms.realestatemanager.data.form.ImagePicker
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.justRun
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class PicturePickerViewModelTest {

    @MockK
    private lateinit var mockActionRepository: ActionRepository

    private lateinit var picturePickerViewModel: PicturePickerViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        justRun { mockActionRepository.requestImagePicking(any()) }

        picturePickerViewModel = PicturePickerViewModel(mockActionRepository)
    }

    @Test
    fun `request image picking from gallery when click on 1st dialog item`() {
        // WHEN
        picturePickerViewModel.onDialogItemClicked(0)

        // THEN
        verify(exactly = 1) { mockActionRepository.requestImagePicking(ImagePicker.GALLERY) }
    }

    @Test
    fun `request image picking from camera when click on 2nd dialog item`() {
        // WHEN
        picturePickerViewModel.onDialogItemClicked(1)

        // THEN
        verify(exactly = 1) { mockActionRepository.requestImagePicking(ImagePicker.CAMERA) }
    }
}