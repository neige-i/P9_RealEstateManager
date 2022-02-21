package com.openclassrooms.realestatemanager.data.form

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.openclassrooms.realestatemanager.util.TestLifecycle.getValueForTesting
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class ActionRepositoryTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val actionRepository = ActionRepository()

    @Test
    fun `trigger exit request when set value to true`() {
        // WHEN
        actionRepository.setExit(true)
        val exitRequest = getValueForTesting(actionRepository.getExitLiveData())

        // THEN
        assertEquals(true, exitRequest)
    }

    @Test
    fun `reset exit request when set value to false`() {
        // WHEN
        actionRepository.setExit(false)
        val exitRequest = getValueForTesting(actionRepository.getExitLiveData())

        // THEN
        assertEquals(false, exitRequest)
    }

    @Test
    fun `trigger image picker request when set value`() {
        // WHEN
        actionRepository.setImagePicker(ImagePicker.CAMERA) // Arbitrary value
        val imagePickerRequest = getValueForTesting(actionRepository.getImagePickerLiveData())

        // THEN
        assertEquals(ImagePicker.CAMERA, imagePickerRequest)
    }

    @Test
    fun `reset image picker request when set value to null`() {
        // WHEN
        actionRepository.setImagePicker(null)
        val imagePickerRequest = getValueForTesting(actionRepository.getImagePickerLiveData())

        // THEN
        assertNull(imagePickerRequest)
    }
}