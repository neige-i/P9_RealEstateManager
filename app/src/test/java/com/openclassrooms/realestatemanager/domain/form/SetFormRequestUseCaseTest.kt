package com.openclassrooms.realestatemanager.domain.form

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.openclassrooms.realestatemanager.data.form.ActionRepository
import com.openclassrooms.realestatemanager.data.form.ImagePicker
import io.mockk.MockKAnnotations
import io.mockk.confirmVerified
import io.mockk.impl.annotations.MockK
import io.mockk.justRun
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SetFormRequestUseCaseTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var mockActionRepository: ActionRepository

    private lateinit var setFormRequestUseCase: SetFormRequestUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        justRun { mockActionRepository.setImagePicker(any()) }

        setFormRequestUseCase = SetFormRequestUseCase(mockActionRepository)
    }

    @After
    fun tearDown() {
        confirmVerified(mockActionRepository)
    }

    @Test
    fun `call setImagePicker(!!) when pick image with a non-null integer`() {
        // WHEN
        setFormRequestUseCase.pickImage(0)

        // THEN
        verify(exactly = 1) { mockActionRepository.setImagePicker(ImagePicker.GALLERY) }
    }

    @Test
    fun `call setImagePicker(null) when pick image with a null integer`() {
        // WHEN
        setFormRequestUseCase.pickImage(null)

        // THEN
        verify(exactly = 1) { mockActionRepository.setImagePicker(null) }
    }
}