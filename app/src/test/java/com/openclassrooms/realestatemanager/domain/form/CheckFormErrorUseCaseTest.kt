package com.openclassrooms.realestatemanager.domain.form

import android.content.Context
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
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class CheckFormErrorUseCaseTest {

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
        // region IN
        private const val DATE_FEB_20 = "20/02/2022"
        private const val DATE_FEB_21 = "21/02/2022"
        private val VALID_FORM = FormRepository.DEFAULT_FORM.copy(
            estateType = "Maniac Mansion",
            pictureList = listOf(FormEntity.PictureEntity(uri = mockk(), description = "Lounge")),
            streetName = "740 Park Avenue",
            city = "New York",
            state = "NY",
            zipcode = "10021",
            country = "United States",
            marketEntryDate = DATE_FEB_20,
            saleDate = "",
            isAvailableForSale = true
        )
        private val VALID_PICTURE = CurrentPictureEntity(
            uri = mockk(),
            description = "Lounge",
            descriptionError = null,
            descriptionCursor = 0
        )
        // endregion IN

        // region OUT
        private const val MANDATORY_ERROR = "mandatory error"
        private const val EMPTY_PHOTO_ERROR = "empty photo error"
        private const val STATE_ERROR = "state error"
        private const val ZIPCODE_ERROR = "zipcode error"
        private const val DATE_ERROR = "date error"
        // endregion OUT
    }

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        every { mockFormRepository.getForm() } returns VALID_FORM
        justRun { mockFormRepository.setTypeError(any()) }
        justRun { mockFormRepository.setPictureListError(any()) }
        justRun { mockFormRepository.setStreetNameError(any()) }
        justRun { mockFormRepository.setCityError(any()) }
        justRun { mockFormRepository.setStateError(any()) }
        justRun { mockFormRepository.setZipcodeError(any()) }
        justRun { mockFormRepository.setCountryError(any()) }
        justRun { mockFormRepository.setEntryDateError(any()) }
        justRun { mockFormRepository.setSaleDateError(any()) }
        every { mockContext.getString(R.string.error_mandatory_field) } returns MANDATORY_ERROR
        every { mockContext.getString(R.string.error_empty_photo_list) } returns EMPTY_PHOTO_ERROR
        every { mockContext.getString(R.string.error_unknown_state) } returns STATE_ERROR
        every { mockContext.getString(R.string.error_incomplete_zipcode) } returns ZIPCODE_ERROR
        every { mockContext.getString(R.string.error_inconsistent_date_order) } returns DATE_ERROR
        every { mockUtilsRepository.stringToDate(DATE_FEB_20) } returns LocalDate.of(2022, 2, 20)
        every { mockUtilsRepository.stringToDate(DATE_FEB_21) } returns LocalDate.of(2022, 2, 21)
        every { mockCurrentPictureRepository.getCurrentPicture() } returns VALID_PICTURE
        justRun { mockCurrentPictureRepository.setDescriptionError(any()) }

        checkFormErrorUseCase = CheckFormErrorUseCase(
            mockFormRepository,
            mockCurrentPictureRepository,
            mockContext
        )
    }

    @After
    fun tearDown() {
        verify(exactly = 1) { mockFormRepository.getForm() }
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

        verify(exactly = 1) { mockFormRepository.setTypeError(null) }
    }

    @Test
    fun `return true when check 1st page with no error`() {
        // WHEN
        val isMainInfoOk = checkFormErrorUseCase.containsNoError(0)

        // THEN
        assertTrue(isMainInfoOk)

        verify(exactly = 1) { mockFormRepository.setTypeError(null) }
    }

    @Test
    fun `return false when check 1st page with empty type`() {
        // GIVEN
        val invalidForm = VALID_FORM.copy(estateType = "")
        every { mockFormRepository.getForm() } returns invalidForm

        // WHEN
        val isMainInfoOk = checkFormErrorUseCase.containsNoError(0)

        // THEN
        assertFalse(isMainInfoOk)

        verify(exactly = 1) { mockContext.getString(R.string.error_mandatory_field) }
        verify(exactly = 1) { mockFormRepository.setTypeError(MANDATORY_ERROR) }
    }

    @Test
    fun `return true when check 2nd page with no error`() {
        // WHEN
        val isDetailInfoOk = checkFormErrorUseCase.containsNoError(1)

        // THEN
        assertTrue(isDetailInfoOk)

        verify(exactly = 1) { mockFormRepository.setPictureListError(null) }
    }

    @Test
    fun `return false when check 2nd page with empty picture list`() {
        // GIVEN
        val invalidForm = VALID_FORM.copy(pictureList = emptyList())
        every { mockFormRepository.getForm() } returns invalidForm

        // WHEN
        val isDetailInfoOk = checkFormErrorUseCase.containsNoError(1)

        // THEN
        assertFalse(isDetailInfoOk)

        verify(exactly = 1) { mockContext.getString(R.string.error_empty_photo_list) }
        verify(exactly = 1) { mockFormRepository.setPictureListError(EMPTY_PHOTO_ERROR) }
    }

    @Test
    fun `return true when check 3rd page with no error`() {
        // WHEN
        val isAddressOk = checkFormErrorUseCase.containsNoError(2)

        // THEN
        assertTrue(isAddressOk)

        verify(exactly = 1) { mockFormRepository.setStreetNameError(null) }
        verify(exactly = 1) { mockFormRepository.setCityError(null) }
        verify(exactly = 1) { mockFormRepository.setStateError(null) }
        verify(exactly = 1) { mockFormRepository.setZipcodeError(null) }
        verify(exactly = 1) { mockFormRepository.setCountryError(null) }
    }

    @Test
    fun `return false when check 3rd page with blank street name`() {
        // GIVEN
        val invalidForm = VALID_FORM.copy(streetName = "    ")
        every { mockFormRepository.getForm() } returns invalidForm

        // WHEN
        val isAddressOk = checkFormErrorUseCase.containsNoError(2)

        // THEN
        assertFalse(isAddressOk)

        verify(exactly = 1) { mockContext.getString(R.string.error_mandatory_field) }
        verify(exactly = 1) { mockFormRepository.setStreetNameError(MANDATORY_ERROR) }
        verify(exactly = 1) { mockFormRepository.setCityError(null) }
        verify(exactly = 1) { mockFormRepository.setStateError(null) }
        verify(exactly = 1) { mockFormRepository.setZipcodeError(null) }
        verify(exactly = 1) { mockFormRepository.setCountryError(null) }
    }

    @Test
    fun `return false when check 3rd page with blank city`() {
        // GIVEN
        val invalidForm = VALID_FORM.copy(city = "  ")
        every { mockFormRepository.getForm() } returns invalidForm

        // WHEN
        val isAddressOk = checkFormErrorUseCase.containsNoError(2)

        // THEN
        assertFalse(isAddressOk)

        verify(exactly = 1) { mockContext.getString(R.string.error_mandatory_field) }
        verify(exactly = 1) { mockFormRepository.setStreetNameError(null) }
        verify(exactly = 1) { mockFormRepository.setCityError(MANDATORY_ERROR) }
        verify(exactly = 1) { mockFormRepository.setStateError(null) }
        verify(exactly = 1) { mockFormRepository.setZipcodeError(null) }
        verify(exactly = 1) { mockFormRepository.setCountryError(null) }
    }

    @Test
    fun `return false when check 3rd page with blank state`() {
        // GIVEN
        val invalidForm = VALID_FORM.copy(state = "  ")
        every { mockFormRepository.getForm() } returns invalidForm

        // WHEN
        val isAddressOk = checkFormErrorUseCase.containsNoError(2)

        // THEN
        assertFalse(isAddressOk)

        verify(exactly = 1) { mockContext.getString(R.string.error_mandatory_field) }
        verify(exactly = 1) { mockFormRepository.setStreetNameError(null) }
        verify(exactly = 1) { mockFormRepository.setCityError(null) }
        verify(exactly = 1) { mockFormRepository.setStateError(MANDATORY_ERROR) }
        verify(exactly = 1) { mockFormRepository.setZipcodeError(null) }
        verify(exactly = 1) { mockFormRepository.setCountryError(null) }
    }

    @Test
    fun `return false when check 3rd page with incorrect state`() {
        // GIVEN
        val invalidForm = VALID_FORM.copy(state = "WHAT")
        every { mockFormRepository.getForm() } returns invalidForm

        // WHEN
        val isAddressOk = checkFormErrorUseCase.containsNoError(2)

        // THEN
        assertFalse(isAddressOk)

        verify(exactly = 1) { mockContext.getString(R.string.error_unknown_state) }
        verify(exactly = 1) { mockFormRepository.setStreetNameError(null) }
        verify(exactly = 1) { mockFormRepository.setCityError(null) }
        verify(exactly = 1) { mockFormRepository.setStateError(STATE_ERROR) }
        verify(exactly = 1) { mockFormRepository.setZipcodeError(null) }
        verify(exactly = 1) { mockFormRepository.setCountryError(null) }
    }

    @Test
    fun `return false when check 3rd page with blank zipcode`() {
        // GIVEN
        val invalidForm = VALID_FORM.copy(zipcode = "  ")
        every { mockFormRepository.getForm() } returns invalidForm

        // WHEN
        val isAddressOk = checkFormErrorUseCase.containsNoError(2)

        // THEN
        assertFalse(isAddressOk)

        verify(exactly = 1) { mockContext.getString(R.string.error_mandatory_field) }
        verify(exactly = 1) { mockFormRepository.setStreetNameError(null) }
        verify(exactly = 1) { mockFormRepository.setCityError(null) }
        verify(exactly = 1) { mockFormRepository.setStateError(null) }
        verify(exactly = 1) { mockFormRepository.setZipcodeError(MANDATORY_ERROR) }
        verify(exactly = 1) { mockFormRepository.setCountryError(null) }
    }

    @Test
    fun `return false when check 3rd page with incomplete zipcode`() {
        // GIVEN
        val invalidForm = VALID_FORM.copy(zipcode = "124") // Zipcode's length < 5
        every { mockFormRepository.getForm() } returns invalidForm

        // WHEN
        val isAddressOk = checkFormErrorUseCase.containsNoError(2)

        // THEN
        assertFalse(isAddressOk)

        verify(exactly = 1) { mockContext.getString(R.string.error_incomplete_zipcode) }
        verify(exactly = 1) { mockFormRepository.setStreetNameError(null) }
        verify(exactly = 1) { mockFormRepository.setCityError(null) }
        verify(exactly = 1) { mockFormRepository.setStateError(null) }
        verify(exactly = 1) { mockFormRepository.setZipcodeError(ZIPCODE_ERROR) }
        verify(exactly = 1) { mockFormRepository.setCountryError(null) }
    }

    @Test
    fun `return false when check 3rd page with blank country`() {
        // GIVEN
        val invalidForm = VALID_FORM.copy(country = "    ")
        every { mockFormRepository.getForm() } returns invalidForm

        // WHEN
        val isAddressOk = checkFormErrorUseCase.containsNoError(2)

        // THEN
        assertFalse(isAddressOk)

        verify(exactly = 1) { mockContext.getString(R.string.error_mandatory_field) }
        verify(exactly = 1) { mockFormRepository.setStreetNameError(null) }
        verify(exactly = 1) { mockFormRepository.setCityError(null) }
        verify(exactly = 1) { mockFormRepository.setStateError(null) }
        verify(exactly = 1) { mockFormRepository.setZipcodeError(null) }
        verify(exactly = 1) { mockFormRepository.setCountryError(MANDATORY_ERROR) }
    }

    @Test
    fun `return true when check 4th page with no error`() {
        // WHEN
        val isSaleOk = checkFormErrorUseCase.containsNoError(3)

        // THEN
        assertTrue(isSaleOk)

        verify(exactly = 1) { mockFormRepository.setEntryDateError(null) }
        verify(exactly = 1) { mockFormRepository.setSaleDateError(null) }
    }

    @Test
    fun `return true when check 4th page with both correct dates`() {
        // GIVEN
        val correctForm = VALID_FORM.copy(saleDate = DATE_FEB_21, isAvailableForSale = false)
        every { mockFormRepository.getForm() } returns correctForm

        // WHEN
        val isSaleOk = checkFormErrorUseCase.containsNoError(3)

        // THEN
        assertTrue(isSaleOk)

        verify(exactly = 2) { mockUtilsRepository.stringToDate(any()) }
        verify(exactly = 1) { mockFormRepository.setEntryDateError(null) }
        verify(exactly = 1) { mockFormRepository.setSaleDateError(null) }
    }

    @Test
    fun `return false when check 4th page with empty entry date`() {
        // GIVEN
        val invalidForm = VALID_FORM.copy(marketEntryDate = "")
        every { mockFormRepository.getForm() } returns invalidForm

        // WHEN
        val isSaleOk = checkFormErrorUseCase.containsNoError(3)

        // THEN
        assertFalse(isSaleOk)

        verify(exactly = 1) { mockContext.getString(R.string.error_mandatory_field) }
        verify(exactly = 1) { mockFormRepository.setEntryDateError(MANDATORY_ERROR) }
        verify(exactly = 1) { mockFormRepository.setSaleDateError(null) }
    }

    @Test
    fun `return false when check 4th page with mandatory empty sale date`() {
        // GIVEN
        val invalidForm = VALID_FORM.copy(saleDate = "", isAvailableForSale = false)
        every { mockFormRepository.getForm() } returns invalidForm

        // WHEN
        val isSaleOk = checkFormErrorUseCase.containsNoError(3)

        // THEN
        assertFalse(isSaleOk)

        verify(exactly = 1) { mockContext.getString(R.string.error_mandatory_field) }
        verify(exactly = 1) { mockFormRepository.setEntryDateError(null) }
        verify(exactly = 1) { mockFormRepository.setSaleDateError(MANDATORY_ERROR) }
    }

    @Test
    fun `return false when check 4th page with incorrect dates`() {
        // GIVEN
        val invalidForm = VALID_FORM.copy(marketEntryDate = DATE_FEB_21, saleDate = DATE_FEB_20)
        every { mockFormRepository.getForm() } returns invalidForm

        // WHEN
        val isSaleOk = checkFormErrorUseCase.containsNoError(3)

        // THEN
        assertFalse(isSaleOk)

        verify(exactly = 2) { mockUtilsRepository.stringToDate(any()) }
        verify(exactly = 2) { mockContext.getString(R.string.error_inconsistent_date_order) }
        verify(exactly = 1) { mockFormRepository.setEntryDateError(DATE_ERROR) }
        verify(exactly = 1) { mockFormRepository.setSaleDateError(DATE_ERROR) }
    }

    @Test
    fun `return true when check picture page with no error`() {
        // WHEN
        val isPictureOk = checkFormErrorUseCase.containsNoError(PageToCheck.PICTURE)

        // THEN
        assertTrue(isPictureOk)

        verify(exactly = 1) { mockCurrentPictureRepository.getCurrentPicture() }
        verify(exactly = 1) { mockCurrentPictureRepository.setDescriptionError(null) }
    }

    @Test
    fun `return false when check picture page with blank description`() {
        // GIVEN
        val invalidPicture = VALID_PICTURE.copy(description = "   ")
        every { mockCurrentPictureRepository.getCurrentPicture() } returns invalidPicture

        // WHEN
        val isPictureOk = checkFormErrorUseCase.containsNoError(PageToCheck.PICTURE)

        // THEN
        assertFalse(isPictureOk)

        verify(exactly = 1) { mockCurrentPictureRepository.getCurrentPicture() }
        verify(exactly = 1) { mockContext.getString(R.string.error_mandatory_field) }
        verify(exactly = 1) { mockCurrentPictureRepository.setDescriptionError(MANDATORY_ERROR) }
    }

    @Test
    fun `throw error when check null picture`() {
        // GIVEN
        every { mockCurrentPictureRepository.getCurrentPicture() } returns null

        // WHEN
        val exception = assertThrows(IllegalStateException::class.java) {
            checkFormErrorUseCase.containsNoError(PageToCheck.PICTURE)
        }

        // THEN
        assertEquals("Picture shouldn't be null when checking for errors", exception.message)

        verify(exactly = 1) { mockCurrentPictureRepository.getCurrentPicture() }
    }
}