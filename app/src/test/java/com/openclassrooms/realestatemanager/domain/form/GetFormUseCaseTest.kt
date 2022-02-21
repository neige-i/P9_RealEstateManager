package com.openclassrooms.realestatemanager.domain.form

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.openclassrooms.realestatemanager.data.form.CurrentPictureEntity
import com.openclassrooms.realestatemanager.data.form.CurrentPictureRepository
import com.openclassrooms.realestatemanager.data.form.FormEntity
import com.openclassrooms.realestatemanager.data.form.FormRepository
import com.openclassrooms.realestatemanager.util.TestLifecycle.getValueForTesting
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GetFormUseCaseTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var mockFormRepository: FormRepository

    @MockK
    private lateinit var mockCurrentPictureRepository: CurrentPictureRepository

    private lateinit var getFormUseCase: GetFormUseCase

    private val formMutableLiveData = MutableLiveData<FormEntity>()
    private val currentPictureMutableLiveData = MutableLiveData<CurrentPictureEntity>()

    companion object {
        // OUT
        private val CURRENT_STATE = FormRepository.DEFAULT_FORM.copy(
            type = "Big house",
            price = "430789.97"
        )
        private val INITIAL_STATE = FormRepository.DEFAULT_FORM
        private val TEST_PICTURE = CurrentPictureEntity(
            uri = mockk(),
            description = "Lounge",
            descriptionError = null,
            descriptionCursor = 0
        )
    }

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        every { mockFormRepository.getFormLiveData() } returns formMutableLiveData
        every { mockFormRepository.getNonNullForm() } returns CURRENT_STATE
        every { mockFormRepository.getInitialState() } returns INITIAL_STATE
        every { mockCurrentPictureRepository.getCurrentPictureLiveData() } returns currentPictureMutableLiveData

        getFormUseCase = GetFormUseCase(mockFormRepository, mockCurrentPictureRepository)
    }

    @Test
    fun `return form when get LiveData`() {
        // GIVEN
        formMutableLiveData.value = INITIAL_STATE

        // WHEN
        val resultForm = getValueForTesting(getFormUseCase.getForm())

        // THEN
        assertEquals(INITIAL_STATE, resultForm)
    }

    @Test
    fun `return non-null form when get current state`() {
        // WHEN
        val resultForm = getFormUseCase.getCurrentState()

        // THEN
        assertEquals(CURRENT_STATE, resultForm)
    }

    @Test
    fun `return ADD form type when get initial state with default form`() {
        // WHEN
        val resultType = getFormUseCase.getType()

        // THEN
        assertEquals(GetFormUseCase.FormType.ADD, resultType)
    }

    @Test
    fun `return EDIT form type when get initial state with different default form`() {
        // GIVEN
        every { mockFormRepository.getInitialState() } returns CURRENT_STATE

        // WHEN
        val resultType = getFormUseCase.getType()

        // THEN
        assertEquals(GetFormUseCase.FormType.EDIT, resultType)
    }

    @Test
    fun `return true when form is modified`() {
        // WHEN
        val hasModifications = getFormUseCase.isModified()

        // THEN
        assertTrue(hasModifications)
    }

    @Test
    fun `return false when form is not modified`() {
        // GIVEN
        every { mockFormRepository.getNonNullForm() } returns INITIAL_STATE

        // WHEN
        val hasModifications = getFormUseCase.isModified()

        // THEN
        assertFalse(hasModifications)
    }

    @Test
    fun `return current picture when get LiveData with non-null value`() {
        // GIVEN
        currentPictureMutableLiveData.value = TEST_PICTURE

        // WHEN
        val pictureResult = getValueForTesting(getFormUseCase.getCurrentPicture())

        // THEN
        assertEquals(TEST_PICTURE, pictureResult)
    }

    @Test
    fun `return null when get LiveData with null value`() {
        // GIVEN
        currentPictureMutableLiveData.value = null

        // WHEN
        val pictureResult = getValueForTesting(getFormUseCase.getCurrentPicture())

        // THEN
        assertNull(pictureResult)
    }
}