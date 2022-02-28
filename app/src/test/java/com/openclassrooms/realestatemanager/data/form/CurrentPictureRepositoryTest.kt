package com.openclassrooms.realestatemanager.data.form

import android.net.Uri
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CurrentPictureRepositoryTest {

    private val currentPictureRepository = CurrentPictureRepository()

    companion object {
        // region IN
        private val DEFAULT_URI = mockk<Uri>()
        private const val DEFAULT_DESCRIPTION = "Lounge"
        // endregion IN

        // region OUT
        private val DEFAULT_PICTURE = CurrentPictureEntity(
            uri = DEFAULT_URI,
            description = DEFAULT_DESCRIPTION,
            descriptionError = null,
            descriptionCursor = 0,
        )
        // endregion OUT
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `return null picture when initialize repository`() = runTest {
        val pictureAsync = currentPictureRepository.getCurrentPictureFlow().first()
        val pictureSync = currentPictureRepository.getCurrentPicture()

        // THEN
        assertNull(pictureAsync)
        assertNull(pictureSync)
    }

    @Test
    fun `return initial picture when initialize`() {
        // WHEN
        initDefaultPicture()
        val picture = currentPictureRepository.getCurrentPicture()

        // THEN
        assertEquals(DEFAULT_PICTURE, picture)
    }

    @Test
    fun `change uri when set a new uri`() {
        // GIVEN
        initDefaultPicture()

        // WHEN
        val newUri = mockk<Uri>()
        currentPictureRepository.setUri(newUri)
        val picture = currentPictureRepository.getCurrentPicture()

        // THEN
        assertEquals(
            DEFAULT_PICTURE.copy(uri = newUri),
            picture
        )
    }

    @Test
    fun `return null when set a new uri on a null picture`() {
        // WHEN
        currentPictureRepository.setUri(mockk())
        val picture = currentPictureRepository.getCurrentPicture()

        // THEN
        assertNull(picture)
    }

    @Test
    fun `change change when set a new description`() {
        // GIVEN
        initDefaultPicture()

        // WHEN
        currentPictureRepository.setDescription("Bedroom", 4)
        val picture = currentPictureRepository.getCurrentPicture()

        // THEN
        assertEquals(
            DEFAULT_PICTURE.copy(description = "Bedroom", descriptionCursor = 4),
            picture
        )
    }

    @Test
    fun `return null when set a new description on a null picture`() {
        // WHEN
        currentPictureRepository.setDescription("Bedroom", 4)
        val picture = currentPictureRepository.getCurrentPicture()

        // THEN
        assertNull(picture)
    }

    @Test
    fun `change description error when set a new error`() {
        // GIVEN
        initDefaultPicture()

        // WHEN
        currentPictureRepository.setDescriptionError("This is an error")
        val picture = currentPictureRepository.getCurrentPicture()

        // THEN
        assertEquals(
            DEFAULT_PICTURE.copy(descriptionError = "This is an error"),
            picture
        )
    }

    @Test
    fun `return null when set a description error on a null picture`() {
        // WHEN
        currentPictureRepository.setDescriptionError("This is an error")
        val picture = currentPictureRepository.getCurrentPicture()

        // THEN
        assertNull(picture)
    }

    @Test
    fun `return null when reset picture`() {
        // GIVEN
        initDefaultPicture()

        // WHEN
        currentPictureRepository.reset()
        val picture = currentPictureRepository.getCurrentPicture()

        // THEN
        assertNull(picture)
    }

    private fun initDefaultPicture() {
        currentPictureRepository.initPicture(DEFAULT_URI, DEFAULT_DESCRIPTION)
    }
}