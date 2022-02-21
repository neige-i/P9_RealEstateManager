package com.openclassrooms.realestatemanager.domain.form

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.openclassrooms.realestatemanager.data.form.ActionRepository
import com.openclassrooms.realestatemanager.data.form.ImagePicker
import com.openclassrooms.realestatemanager.util.TestLifecycle.getValueForTesting
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GetFormRequestUseCaseTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var mockActionRepository: ActionRepository

    private lateinit var getFormRequestUseCase: GetFormRequestUseCase

    private val exitMutableLiveData = MutableLiveData<Boolean>()
    private val imagePickerMutableLiveData = MutableLiveData<ImagePicker>()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        every { mockActionRepository.getExitLiveData() } returns exitMutableLiveData
        every { mockActionRepository.getImagePickerLiveData() } returns imagePickerMutableLiveData

        getFormRequestUseCase = GetFormRequestUseCase(mockActionRepository)
    }

    @Test
    fun `return true when get exit request with true value`() {
        // GIVEN
        exitMutableLiveData.value = true

        // WHEN
        val exitRequest = getValueForTesting(getFormRequestUseCase.getExit())

        // THEN
        assertEquals(true, exitRequest)
    }

    @Test
    fun `return false when get exit request with false value`() {
        // GIVEN
        exitMutableLiveData.value = false

        // WHEN
        val exitRequest = getValueForTesting(getFormRequestUseCase.getExit())

        // THEN
        assertEquals(false, exitRequest)
    }

    @Test
    fun `return image picker when get picker request with non-null value`() {
        // GIVEN
        imagePickerMutableLiveData.value = ImagePicker.CAMERA // Arbitrary non-null value

        // WHEN
        val imagePickerRequest = getValueForTesting(getFormRequestUseCase.getImagePicker())

        // THEN
        assertEquals(ImagePicker.CAMERA, imagePickerRequest)
    }

    @Test
    fun `return null when get picker request with null value`() {
        // GIVEN
        imagePickerMutableLiveData.value = null

        // WHEN
        val imagePickerRequest = getValueForTesting(getFormRequestUseCase.getImagePicker())

        // THEN
        assertNull(imagePickerRequest)
    }
}