package com.openclassrooms.realestatemanager.data.form

import com.openclassrooms.realestatemanager.util.TestCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ActionRepositoryTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val actionRepository = ActionRepository()

    @Test
    fun `trigger image picker request when select camera`() = runTest {
        // WHEN
        actionRepository.requestImagePicking(ImagePicker.CAMERA)
        val imagePickerRequest = actionRepository.getImagePickerFlow().first()

        // THEN
        assertEquals(ImagePicker.CAMERA, imagePickerRequest)
    }

    @Test
    fun `trigger image picker request when select gallery`() = runTest {
        // WHEN
        actionRepository.requestImagePicking(ImagePicker.GALLERY)
        val imagePickerRequest = actionRepository.getImagePickerFlow().first()

        // THEN
        assertEquals(ImagePicker.GALLERY, imagePickerRequest)
    }

    @Test
    fun `reset image picker request when flush it`() = runTest {
        // WHEN
        actionRepository.flushImagePickerFlow()
        val imagePickerRequest = actionRepository.getImagePickerFlow().first()

        // THEN
        assertNull(imagePickerRequest)
    }

    @Test
    fun `trigger picture opening when request it`() = runTest {
        // WHEN
        actionRepository.requestPictureOpening()
        val pictureOpeningRequest = actionRepository.getPictureOpenerFlow().first()

        // THEN
        assertTrue(pictureOpeningRequest)
    }
}