package com.openclassrooms.realestatemanager.domain.form

import com.openclassrooms.realestatemanager.data.form.CurrentPictureEntity
import com.openclassrooms.realestatemanager.data.form.CurrentPictureRepository
import com.openclassrooms.realestatemanager.data.form.FormRepository
import com.openclassrooms.realestatemanager.util.TestCoroutineRule
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class GetFormUseCaseTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @MockK
    private lateinit var mockFormRepository: FormRepository

    @MockK
    private lateinit var mockCurrentPictureRepository: CurrentPictureRepository

    private lateinit var getFormUseCase: GetFormUseCase

    companion object {
        // region IN
        private val CURRENT_FORM = FormRepository.DEFAULT_FORM.copy(estateType = "Big house")
        private val TEST_PICTURE = CurrentPictureEntity(
            uri = mockk(),
            description = "Lounge",
            descriptionError = null,
            descriptionCursor = 0
        )
        // endregion IN
    }

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        every { mockFormRepository.getFormFlow() } returns flowOf(CURRENT_FORM)
        every { mockFormRepository.getForm() } returns CURRENT_FORM
        every { mockFormRepository.getInitialState() } returns FormRepository.DEFAULT_FORM
        every { mockCurrentPictureRepository.getPictureFlow() } returns flowOf(TEST_PICTURE)

        getFormUseCase = GetFormUseCase(mockFormRepository, mockCurrentPictureRepository)
    }

    @After
    fun tearDown() {
        confirmVerified(mockFormRepository, mockCurrentPictureRepository)
    }

    @Test
    fun `return form when collect value`() = runTest {
        // WHEN
        val form = getFormUseCase.getFormFlow().first()

        // THEN
        assertEquals(CURRENT_FORM, form)

        verify(exactly = 1) { mockFormRepository.getFormFlow() }
    }

    @Test
    fun `return modified ADD form info when collect value`() {
        // WHEN
        val formInfo = getFormUseCase.getFormInfo()

        // THEN
        assertEquals(
            FormInfo(
                formType = FormInfo.FormType.ADD,
                isModified = true,
                estateType = "Big house"
            ),
            formInfo
        )

        verify(exactly = 1) { mockFormRepository.getForm() }
        verify(exactly = 1) { mockFormRepository.getInitialState() }
    }

    @Test
    fun `return unmodified ADD form info when collect value`() {
        // GIVEN
        every { mockFormRepository.getForm() } returns FormRepository.DEFAULT_FORM

        // WHEN
        val formInfo = getFormUseCase.getFormInfo()

        // THEN
        assertEquals(
            FormInfo(
                formType = FormInfo.FormType.ADD,
                isModified = false,
                estateType = ""
            ),
            formInfo
        )

        verify(exactly = 1) { mockFormRepository.getForm() }
        verify(exactly = 1) { mockFormRepository.getInitialState() }
    }

    @Test
    fun `return modified EDIT form info when collect value`() {
        // GIVEN
        every { mockFormRepository.getInitialState() } returns CURRENT_FORM
        every { mockFormRepository.getForm() } returns CURRENT_FORM.copy(estateType = "Very big house")

        // WHEN
        val formInfo = getFormUseCase.getFormInfo()

        // THEN
        assertEquals(
            FormInfo(
                formType = FormInfo.FormType.EDIT,
                isModified = true,
                estateType = "Very big house"
            ),
            formInfo
        )

        verify(exactly = 1) { mockFormRepository.getForm() }
        verify(exactly = 1) { mockFormRepository.getInitialState() }
    }

    @Test
    fun `return unmodified EDIT form info when collect value`() {
        // GIVEN
        every { mockFormRepository.getInitialState() } returns CURRENT_FORM

        // WHEN
        val formInfo = getFormUseCase.getFormInfo()

        // THEN
        assertEquals(
            FormInfo(
                formType = FormInfo.FormType.EDIT,
                isModified = false,
                estateType = "Big house"
            ),
            formInfo
        )

        verify(exactly = 1) { mockFormRepository.getForm() }
        verify(exactly = 1) { mockFormRepository.getInitialState() }
    }

    @Test
    fun `return current picture when get async with non-null value`() = runTest {
        // WHEN
        val picture = getFormUseCase.getCurrentPictureFlow().first()

        // THEN
        assertEquals(TEST_PICTURE, picture)

        verify(exactly = 1) { mockCurrentPictureRepository.getPictureFlow() }
    }
}