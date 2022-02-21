package com.openclassrooms.realestatemanager.domain.form

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.UtilsRepository
import com.openclassrooms.realestatemanager.data.form.CurrentPictureEntity
import com.openclassrooms.realestatemanager.data.form.CurrentPictureRepository
import com.openclassrooms.realestatemanager.data.form.FormEntity
import com.openclassrooms.realestatemanager.data.form.FormRepository
import com.openclassrooms.realestatemanager.domain.form.CheckFormErrorUseCase.PageToCheck
import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class CheckFormErrorUseCaseTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var mockFormRepository: FormRepository

    @MockK
    private lateinit var mockCurrentPictureRepository: CurrentPictureRepository

    @MockK
    private lateinit var mockUtilsRepository: UtilsRepository

    @MockK
    private lateinit var mockContext: Context

    private lateinit var checkFormErrorUseCase: CheckFormErrorUseCase

    companion object {
        // OUT
        // All following attributes need to be verified
        private val VALID_FORM = FormRepository.DEFAULT_FORM.copy(
            type = "Maniac Mansion",
            pictureList = listOf(FormEntity.PictureEntity(uri = mockk(), description = "Lounge")),
            streetName = "740 Park Avenue",
            city = "New York",
            state = "NY",
            zipcode = "10021",
            country = "United States",
            marketEntryDate = "21/02/2022"
        )
        private val VALID_CURRENT_PICTURE = CurrentPictureEntity(
            uri = mockk(),
            description = "Lounge",
            descriptionError = null,
            descriptionCursor = 0
        )
    }

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        every { mockFormRepository.getNonNullForm() } returns VALID_FORM
        justRun { mockFormRepository.setForm(any()) }
        every { mockContext.getString(R.string.error_mandatory_field) } returns "mandatory error"
        every { mockContext.getString(R.string.error_empty_photo_list) } returns "empty error"
        every { mockContext.getString(R.string.error_unknown_state) } returns "state error"
        every { mockContext.getString(R.string.error_incomplete_zipcode) } returns "zipcode error"
        every { mockContext.getString(R.string.error_inconsistent_date_order) } returns "date error"
        every { mockUtilsRepository.stringToDate("20/02/2022") } returns LocalDate.of(2022, 2, 20)
        every { mockUtilsRepository.stringToDate("21/02/2022") } returns LocalDate.of(2022, 2, 21)
        every { mockCurrentPictureRepository.getNonNullCurrentPicture() } returns VALID_CURRENT_PICTURE
        justRun { mockCurrentPictureRepository.setCurrentPicture(any()) }

        checkFormErrorUseCase = CheckFormErrorUseCase(
            mockFormRepository,
            mockCurrentPictureRepository,
            mockUtilsRepository,
            mockContext
        )
    }

    @After
    fun tearDown() {
        verify(exactly = 1) { mockFormRepository.getNonNullForm() }
        confirmVerified(
            mockFormRepository,
            mockCurrentPictureRepository,
            mockUtilsRepository,
            mockContext
        )
    }

    @Test
    fun `return true when check main page with no error`() {
        // WHEN
        val isMainInfoOk = checkFormErrorUseCase.containsNoError(PageToCheck.MAIN)

        // THEN
        assertTrue(isMainInfoOk)

        verify(exactly = 1) { mockFormRepository.setForm(VALID_FORM) }
    }

    @Test
    fun `return true when check 1st page with no error`() {
        // WHEN
        val isMainInfoOk = checkFormErrorUseCase.containsNoError(0)

        // THEN
        assertTrue(isMainInfoOk)

        verify(exactly = 1) { mockFormRepository.setForm(VALID_FORM) }
    }

    @Test
    fun `return false when check 1st page with empty type`() {
        // GIVEN
        val invalidForm = VALID_FORM.copy(type = "")
        every { mockFormRepository.getNonNullForm() } returns invalidForm

        // WHEN
        val isMainInfoOk = checkFormErrorUseCase.containsNoError(0)

        // THEN
        assertFalse(isMainInfoOk)

        verify(exactly = 1) { mockContext.getString(R.string.error_mandatory_field) }
        verify(exactly = 1) {
            mockFormRepository.setForm(invalidForm.copy(typeError = "mandatory error"))
        }
    }

    @Test
    fun `return true when check 2nd page with no error`() {
        // WHEN
        val isDetailInfoOk = checkFormErrorUseCase.containsNoError(1)

        // THEN
        assertTrue(isDetailInfoOk)

        verify(exactly = 1) { mockFormRepository.setForm(VALID_FORM) }
    }

    @Test
    fun `return false when check 2nd page with empty picture list`() {
        // GIVEN
        val invalidForm = VALID_FORM.copy(pictureList = emptyList())
        every { mockFormRepository.getNonNullForm() } returns invalidForm

        // WHEN
        val isDetailInfoOk = checkFormErrorUseCase.containsNoError(1)

        // THEN
        assertFalse(isDetailInfoOk)

        verify(exactly = 1) { mockContext.getString(R.string.error_empty_photo_list) }
        verify(exactly = 1) {
            mockFormRepository.setForm(invalidForm.copy(pictureListError = "empty error"))
        }
    }

    @Test
    fun `return true when check 3rd page with no error`() {
        // WHEN
        val isAddressOk = checkFormErrorUseCase.containsNoError(2)

        // THEN
        assertTrue(isAddressOk)

        verify(exactly = 1) { mockFormRepository.setForm(VALID_FORM) }
    }

    @Test
    fun `return false when check 3rd page with blank street name`() {
        // GIVEN
        val invalidForm = VALID_FORM.copy(streetName = "    ")
        every { mockFormRepository.getNonNullForm() } returns invalidForm

        // WHEN
        val isAddressOk = checkFormErrorUseCase.containsNoError(2)

        // THEN
        assertFalse(isAddressOk)

        verify(exactly = 1) { mockContext.getString(R.string.error_mandatory_field) }
        verify(exactly = 1) {
            mockFormRepository.setForm(invalidForm.copy(streetNameError = "mandatory error"))
        }
    }

    @Test
    fun `return false when check 3rd page with blank city`() {
        // GIVEN
        val invalidForm = VALID_FORM.copy(city = "  ")
        every { mockFormRepository.getNonNullForm() } returns invalidForm

        // WHEN
        val isAddressOk = checkFormErrorUseCase.containsNoError(2)

        // THEN
        assertFalse(isAddressOk)

        verify(exactly = 1) { mockContext.getString(R.string.error_mandatory_field) }
        verify(exactly = 1) {
            mockFormRepository.setForm(invalidForm.copy(cityError = "mandatory error"))
        }
    }

    @Test
    fun `return false when check 3rd page with blank state`() {
        // GIVEN
        val invalidForm = VALID_FORM.copy(state = "  ")
        every { mockFormRepository.getNonNullForm() } returns invalidForm

        // WHEN
        val isAddressOk = checkFormErrorUseCase.containsNoError(2)

        // THEN
        assertFalse(isAddressOk)

        verify(exactly = 1) { mockContext.getString(R.string.error_mandatory_field) }
        verify(exactly = 1) {
            mockFormRepository.setForm(invalidForm.copy(stateError = "mandatory error"))
        }
    }

    @Test
    fun `return false when check 3rd page with incorrect state`() {
        // GIVEN
        val invalidForm = VALID_FORM.copy(state = "WHAT")
        every { mockFormRepository.getNonNullForm() } returns invalidForm

        // WHEN
        val isAddressOk = checkFormErrorUseCase.containsNoError(2)

        // THEN
        assertFalse(isAddressOk)

        verify(exactly = 1) { mockContext.getString(R.string.error_unknown_state) }
        verify(exactly = 1) {
            mockFormRepository.setForm(invalidForm.copy(stateError = "state error"))
        }
    }

    @Test
    fun `return false when check 3rd page with blank zipcode`() {
        // GIVEN
        val invalidForm = VALID_FORM.copy(zipcode = "  ")
        every { mockFormRepository.getNonNullForm() } returns invalidForm

        // WHEN
        val isAddressOk = checkFormErrorUseCase.containsNoError(2)

        // THEN
        assertFalse(isAddressOk)

        verify(exactly = 1) { mockContext.getString(R.string.error_mandatory_field) }
        verify(exactly = 1) {
            mockFormRepository.setForm(invalidForm.copy(zipcodeError = "mandatory error"))
        }
    }

    @Test
    fun `return false when check 3rd page with incomplete zipcode`() {
        // GIVEN
        val invalidForm = VALID_FORM.copy(zipcode = "124") // Zipcode's length < 5
        every { mockFormRepository.getNonNullForm() } returns invalidForm

        // WHEN
        val isAddressOk = checkFormErrorUseCase.containsNoError(2)

        // THEN
        assertFalse(isAddressOk)

        verify(exactly = 1) { mockContext.getString(R.string.error_incomplete_zipcode) }
        verify(exactly = 1) {
            mockFormRepository.setForm(invalidForm.copy(zipcodeError = "zipcode error"))
        }
    }

    @Test
    fun `return false when check 3rd page with blank country`() {
        // GIVEN
        val invalidForm = VALID_FORM.copy(country = "    ")
        every { mockFormRepository.getNonNullForm() } returns invalidForm

        // WHEN
        val isAddressOk = checkFormErrorUseCase.containsNoError(2)

        // THEN
        assertFalse(isAddressOk)

        verify(exactly = 1) { mockContext.getString(R.string.error_mandatory_field) }
        verify(exactly = 1) {
            mockFormRepository.setForm(invalidForm.copy(countryError = "mandatory error"))
        }
    }

    @Test
    fun `return true when check 4th page with no error`() {
        // WHEN
        val isSaleOk = checkFormErrorUseCase.containsNoError(3)

        // THEN
        assertTrue(isSaleOk)

        verify(exactly = 1) { mockFormRepository.setForm(VALID_FORM) }
    }

    @Test
    fun `return true when check 4th page with both correct dates`() {
        // GIVEN
        val correctForm = VALID_FORM.copy(marketEntryDate = "20/02/2022", saleDate = "21/02/2022")
        every { mockFormRepository.getNonNullForm() } returns correctForm

        // WHEN
        val isSaleOk = checkFormErrorUseCase.containsNoError(3)

        // THEN
        assertTrue(isSaleOk)

        verify(exactly = 2) { mockUtilsRepository.stringToDate(any()) }
        verify(exactly = 1) { mockFormRepository.setForm(correctForm) }
    }

    @Test
    fun `return false when check 4th page with empty entry date`() {
        // GIVEN
        val invalidForm = VALID_FORM.copy(marketEntryDate = "")
        every { mockFormRepository.getNonNullForm() } returns invalidForm

        // WHEN
        val isSaleOk = checkFormErrorUseCase.containsNoError(3)

        // THEN
        assertFalse(isSaleOk)

        verify(exactly = 1) { mockContext.getString(R.string.error_mandatory_field) }
        verify(exactly = 1) {
            mockFormRepository.setForm(invalidForm.copy(marketEntryDateError = "mandatory error"))
        }
    }

    @Test
    fun `return false when check 4th page with empty sale date`() {
        // GIVEN
        val invalidForm = VALID_FORM.copy(saleDate = "", isAvailableForSale = false)
        every { mockFormRepository.getNonNullForm() } returns invalidForm

        // WHEN
        val isSaleOk = checkFormErrorUseCase.containsNoError(3)

        // THEN
        assertFalse(isSaleOk)

        verify(exactly = 1) { mockContext.getString(R.string.error_mandatory_field) }
        verify(exactly = 1) {
            mockFormRepository.setForm(invalidForm.copy(saleDateError = "mandatory error"))
        }
    }

    @Test
    fun `return false when check 4th page with incorrect dates`() {
        // GIVEN
        val invalidForm = VALID_FORM.copy(saleDate = "20/02/2022", isAvailableForSale = false)
        every { mockFormRepository.getNonNullForm() } returns invalidForm

        // WHEN
        val isSaleOk = checkFormErrorUseCase.containsNoError(3)

        // THEN
        assertFalse(isSaleOk)

        verify(exactly = 2) { mockUtilsRepository.stringToDate(any()) }
        verify(exactly = 2) { mockContext.getString(R.string.error_inconsistent_date_order) }
        verify(exactly = 1) {
            mockFormRepository.setForm(
                invalidForm.copy(
                    marketEntryDateError = "date error",
                    saleDateError = "date error"
                )
            )
        }
    }

    @Test
    fun `return true when check picture page with no error`() {
        // WHEN
        val isPictureOk = checkFormErrorUseCase.containsNoError(PageToCheck.PICTURE)

        // THEN
        assertTrue(isPictureOk)

        verify(exactly = 1) { mockCurrentPictureRepository.getNonNullCurrentPicture() }
        verify(exactly = 1) { mockCurrentPictureRepository.setCurrentPicture(VALID_CURRENT_PICTURE) }
    }

    @Test
    fun `return false when check picture page with blank description`() {
        // GIVEN
        val invalidPicture = VALID_CURRENT_PICTURE.copy(description = "   ")
        every { mockCurrentPictureRepository.getNonNullCurrentPicture() } returns invalidPicture

        // WHEN
        val isPictureOk = checkFormErrorUseCase.containsNoError(PageToCheck.PICTURE)

        // THEN
        assertFalse(isPictureOk)

        verify(exactly = 1) { mockCurrentPictureRepository.getNonNullCurrentPicture() }
        verify(exactly = 1) { mockContext.getString(R.string.error_mandatory_field) }
        verify(exactly = 1) {
            mockCurrentPictureRepository.setCurrentPicture(
                invalidPicture.copy(descriptionError = "mandatory error")
            )
        }
    }
}