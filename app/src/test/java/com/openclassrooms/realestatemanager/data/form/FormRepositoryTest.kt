package com.openclassrooms.realestatemanager.data.form

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.openclassrooms.realestatemanager.util.TestLifecycle.getValueForTesting
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class FormRepositoryTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val formRepository = FormRepository()

    companion object {
        private val TEST_FORM = FormRepository.DEFAULT_FORM
    }

    @Test
    fun `return form when observe after setting a non-null value`() {
        // GIVEN
        formRepository.setForm(TEST_FORM)

        // WHEN
        val resultForm = getValueForTesting(formRepository.getFormLiveData())

        // THEN
        assertEquals(TEST_FORM, resultForm)
    }

    @Test
    fun `return null when observe without setting a value`() {
        // WHEN
        val resultForm = getValueForTesting(formRepository.getFormLiveData())

        // THEN
        assertNull(resultForm)
    }

    @Test
    fun `return form when safely fetch after setting a non-null value`() {
        // GIVEN
        formRepository.setForm(TEST_FORM)

        // WHEN
        val resultForm = formRepository.getForm()

        // THEN
        assertEquals(TEST_FORM, resultForm)
    }

    @Test
    fun `return null when safely fetch without setting a value`() {
        // WHEN
        val resultForm = formRepository.getForm()

        // THEN
        assertNull(resultForm)
    }

    @Test
    fun `return form when unsafely fetch after setting a non-null value`() {
        // GIVEN
        formRepository.setForm(TEST_FORM)

        // WHEN
        val resultForm = formRepository.getNonNullForm()

        // THEN
        assertEquals(TEST_FORM, resultForm)
    }

    @Test
    fun `throw exception when unsafely fetch without setting a value`() {
        // WHEN
        val exception = assertThrows(IllegalStateException::class.java) {
            formRepository.getNonNullForm()
        }

        // THEN
        assertEquals(
            "The form has not been set. Please call setForm() before accessing it",
            exception.message
        )
    }

    @Test
    fun `return initial form when fetch after initialization`() {
        // GIVEN
        formRepository.initForm(TEST_FORM)

        // WHEN
        val resultForm = formRepository.getInitialState()

        // THEN
        assertEquals(TEST_FORM, resultForm)
    }

    @Test
    fun `throw exception when fetch without initialization`() {
        // WHEN
        val exception = assertThrows(IllegalStateException::class.java) {
            formRepository.getInitialState()
        }

        // THEN
        assertEquals(
            "The form has not been initialized. Please call initForm() before accessing it",
            exception.message
        )
    }

    @Test
    fun `return null when reset`() {
        // GIVEN
        formRepository.initForm(TEST_FORM)
        formRepository.setForm(TEST_FORM)

        // WHEN
        formRepository.resetForm()

        assertThrows(IllegalStateException::class.java) { formRepository.getInitialState() }
        val currentForm = formRepository.getForm()

        // THEN
        assertNull(currentForm)
    }

    @Test
    fun `return position when fetch after setting a value`() {
        // GIVEN
        formRepository.setCurrentPicturePosition(4)

        // WHEN
        val picturePosition = formRepository.getCurrentPicturePosition()

        // THEN
        assertEquals(4, picturePosition)
    }

    @Test
    fun `return -1 when fetch without setting a value`() {
        // WHEN
        val picturePosition = formRepository.getCurrentPicturePosition()

        // THEN
        assertEquals(-1, picturePosition)
    }
}