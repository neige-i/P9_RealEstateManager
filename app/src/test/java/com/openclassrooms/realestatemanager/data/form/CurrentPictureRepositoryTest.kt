package com.openclassrooms.realestatemanager.data.form

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.openclassrooms.realestatemanager.util.TestLifecycle.getValueForTesting
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class CurrentPictureRepositoryTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val currentPictureRepository = CurrentPictureRepository()

    companion object {
        private val TEST_PICTURE = CurrentPictureEntity(
            uri = mockk(),
            description = "Lounge",
            descriptionError = null,
            descriptionCursor = 0
        )
    }

    @Test
    fun `return picture when observe after setting a non-null value`() {
        // GIVEN
        currentPictureRepository.setCurrentPicture(TEST_PICTURE)

        // WHEN
        val resultPicture = getValueForTesting(currentPictureRepository.getCurrentPictureLiveData())

        // THEN
        assertEquals(TEST_PICTURE, resultPicture)
    }

    @Test
    fun `return null when observe after setting a null value`() {
        // GIVEN
        currentPictureRepository.setCurrentPicture(null)

        // WHEN
        val resultPicture = getValueForTesting(currentPictureRepository.getCurrentPictureLiveData())

        // THEN
        assertNull(resultPicture)
    }

    @Test
    fun `return picture when safely fetch after setting a non-null value`() {
        // GIVEN
        currentPictureRepository.setCurrentPicture(TEST_PICTURE)

        // WHEN
        val safePicture = currentPictureRepository.getCurrentPicture()

        // THEN
        assertEquals(TEST_PICTURE, safePicture)
    }

    @Test
    fun `return null when safely fetch after setting a null value`() {
        // GIVEN
        currentPictureRepository.setCurrentPicture(null)

        // WHEN
        val safePicture = currentPictureRepository.getCurrentPicture()

        // THEN
        assertNull(safePicture)
    }

    @Test
    fun `return picture when unsafely fetch after setting a non-null value`() {
        // GIVEN
        currentPictureRepository.setCurrentPicture(TEST_PICTURE)

        // WHEN
        val unSafePicture = currentPictureRepository.getNonNullCurrentPicture()

        // THEN
        assertEquals(TEST_PICTURE, unSafePicture)
    }

    @Test
    fun `throw exception when unsafely fetch after setting a null value`() {
        // GIVEN
        currentPictureRepository.setCurrentPicture(null)

        // WHEN
        val exception = assertThrows(IllegalStateException::class.java) {
            currentPictureRepository.getNonNullCurrentPicture()
        }

        // THEN
        assertEquals("Picture is not initialized!", exception.message)
    }
}