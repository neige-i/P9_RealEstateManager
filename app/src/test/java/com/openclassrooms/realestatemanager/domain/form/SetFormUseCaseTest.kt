package com.openclassrooms.realestatemanager.domain.form

import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.openclassrooms.realestatemanager.data.form.CurrentPictureEntity
import com.openclassrooms.realestatemanager.data.form.CurrentPictureRepository
import com.openclassrooms.realestatemanager.data.form.FormEntity
import com.openclassrooms.realestatemanager.data.form.FormRepository
import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SetFormUseCaseTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var mockFormRepository: FormRepository

    @MockK
    private lateinit var mockCurrentPictureRepository: CurrentPictureRepository

    private lateinit var setFormUseCase: SetFormUseCase

    companion object {
        // OUT
        private val mockUri = mockk<Uri>()
        private val CURRENT_STATE = FormRepository.DEFAULT_FORM.copy(
            type = "Flat",
            price = "99000.99",
            area = "42",
            description = "Great flat",
            pictureList = listOf(FormEntity.PictureEntity(mockUri, "Lounge")),
            pictureListError = "ERROR",
            totalRoomCount = 5,
            streetName = "740 Park Avenue",
            additionalAddressInfo = "Apt 6/7A",
            city = "New York",
            cityError = "ERROR",
            state = "NY",
            zipcode = "10021",
            country = "United States",
            pointsOfInterests = listOf(4, 8, 13)
        )
        private val CURRENT_PICTURE = CurrentPictureEntity(
            uri = mockUri,
            description = "Living room",
            descriptionError = null,
            descriptionCursor = 0
        )
    }

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        justRun { mockFormRepository.initForm(any()) }
        every { mockFormRepository.getForm() } returns CURRENT_STATE
        every { mockFormRepository.getNonNullForm() } returns CURRENT_STATE
        every { mockFormRepository.getCurrentPicturePosition() } returns 1
        justRun { mockFormRepository.setForm(any()) }
        justRun { mockFormRepository.resetForm() }
        justRun { mockFormRepository.setCurrentPicturePosition(any()) }
        justRun { mockCurrentPictureRepository.setCurrentPicture(any()) }
        every { mockCurrentPictureRepository.getCurrentPicture() } returns CURRENT_PICTURE
        every { mockCurrentPictureRepository.getNonNullCurrentPicture() } returns CURRENT_PICTURE

        setFormUseCase = SetFormUseCase(mockFormRepository, mockCurrentPictureRepository)
    }

    @After
    fun tearDown() {
        confirmVerified(mockFormRepository, mockCurrentPictureRepository)
    }

    @Test
    fun `update initial & current state when init ADD form with non-null current state`() {
        // WHEN
        setFormUseCase.initAddForm()

        // THEN
        verify(exactly = 1) { mockFormRepository.initForm(FormRepository.DEFAULT_FORM) }
        verify(exactly = 1) { mockFormRepository.getForm() }
        verify(exactly = 1) {
            mockFormRepository.setForm(
                // Errors are reset
                CURRENT_STATE.copy(
                    pictureListError = null,
                    cityError = null
                )
            )
        }
    }

    @Test
    fun `update initial & current state when init ADD form with null current state`() {
        // GIVEN
        every { mockFormRepository.getForm() } returns null

        // WHEN
        setFormUseCase.initAddForm()

        // THEN
        verify(exactly = 1) { mockFormRepository.initForm(FormRepository.DEFAULT_FORM) }
        verify(exactly = 1) { mockFormRepository.getForm() }
        verify(exactly = 1) { mockFormRepository.setForm(FormRepository.DEFAULT_FORM) }
    }

    @Test
    fun `call repository method when reset form`() {
        // WHEN
        setFormUseCase.reset()

        // THEN
        verify(exactly = 1) { mockFormRepository.resetForm() }
    }

    @Test
    fun `set form's type when update type`() {
        // WHEN
        setFormUseCase.updateType("new type")

        // THEN
        verify(exactly = 1) { mockFormRepository.getNonNullForm() }
        verify(exactly = 1) { mockFormRepository.setForm(CURRENT_STATE.copy(type = "new type")) }
    }

    @Test
    fun `set form's price when update with different value`() {
        // WHEN
        setFormUseCase.updatePrice("155000", 0)

        // THEN
        verify(exactly = 2) { mockFormRepository.getNonNullForm() }
        verify(exactly = 1) {
            mockFormRepository.setForm(
                CURRENT_STATE.copy(price = "155000", priceCursor = 0)
            )
        }
    }

    @Test
    fun `do NOT set form's price when update with same value`() {
        // WHEN
        setFormUseCase.updatePrice("99000.99", 0)

        // THEN
        verify(exactly = 1) { mockFormRepository.getNonNullForm() }
        verify(exactly = 0) {
            mockFormRepository.setForm(
                CURRENT_STATE.copy(price = "99000.99", priceCursor = 0)
            )
        }
    }

    @Test
    fun `set form's area when update with different value`() {
        // WHEN
        setFormUseCase.updateArea("50", 2)

        // THEN
        verify(exactly = 2) { mockFormRepository.getNonNullForm() }
        verify(exactly = 1) {
            mockFormRepository.setForm(
                CURRENT_STATE.copy(area = "50", areaCursor = 2)
            )
        }
    }

    @Test
    fun `do NOT set form's area when update with same value`() {
        // WHEN
        setFormUseCase.updateArea("42", 0)

        // THEN
        verify(exactly = 1) { mockFormRepository.getNonNullForm() }
        verify(exactly = 0) {
            mockFormRepository.setForm(
                CURRENT_STATE.copy(area = "99000.99", areaCursor = 0)
            )
        }
    }

    @Test
    fun `add total room when increment count`() {
        // WHEN
        setFormUseCase.incTotalRoomCount()

        // THEN
        verify(exactly = 2) { mockFormRepository.getNonNullForm() }
        verify(exactly = 1) { mockFormRepository.setForm(CURRENT_STATE.copy(totalRoomCount = 6)) }
    }

    @Test
    fun `remove total room when decrement count with rooms to spare`() {
        // WHEN
        setFormUseCase.decTotalRoomCount()

        // THEN
        verify(exactly = 3) { mockFormRepository.getNonNullForm() }
        verify(exactly = 1) { mockFormRepository.setForm(CURRENT_STATE.copy(totalRoomCount = 4)) }
    }

    @Test
    fun `do NOT remove total room when decrement count without rooms to spare`() {
        // GIVEN
        every { mockFormRepository.getNonNullForm() } returns CURRENT_STATE.copy(bathroomCount = 5)

        // WHEN
        setFormUseCase.decTotalRoomCount()

        // THEN
        verify(exactly = 1) { mockFormRepository.getNonNullForm() }
        verify(exactly = 0) { mockFormRepository.setForm(CURRENT_STATE.copy(totalRoomCount = 4)) }
    }

    @Test
    fun `add bathroom when increment count with rooms to spare`() {
        // WHEN
        setFormUseCase.incBathroomCount()

        // THEN
        verify(exactly = 3) { mockFormRepository.getNonNullForm() }
        verify(exactly = 1) { mockFormRepository.setForm(CURRENT_STATE.copy(bathroomCount = 1)) }
    }

    @Test
    fun `do NOT add bathroom when increment count without rooms to spare`() {
        // GIVEN
        every { mockFormRepository.getNonNullForm() } returns CURRENT_STATE.copy(bedroomCount = 5)

        // WHEN
        setFormUseCase.incBathroomCount()

        // THEN
        verify(exactly = 1) { mockFormRepository.getNonNullForm() }
        verify(exactly = 0) { mockFormRepository.setForm(CURRENT_STATE.copy(bathroomCount = 1)) }
    }

    @Test
    fun `remove bathroom when decrement count with bathrooms to spare`() {
        // GIVEN
        every { mockFormRepository.getNonNullForm() } returns CURRENT_STATE.copy(bathroomCount = 2)

        // WHEN
        setFormUseCase.decBathroomCount()

        // THEN
        verify(exactly = 3) { mockFormRepository.getNonNullForm() }
        verify(exactly = 1) { mockFormRepository.setForm(CURRENT_STATE.copy(bathroomCount = 1)) }
    }

    @Test
    fun `do NOT remove bathroom when decrement count without bathrooms to spare`() {
        // WHEN
        setFormUseCase.decBathroomCount()

        // THEN
        verify(exactly = 1) { mockFormRepository.getNonNullForm() }
        verify(exactly = 0) { mockFormRepository.setForm(CURRENT_STATE.copy(bathroomCount = -1)) }
    }

    @Test
    fun `add bedroom when increment count with rooms to spare`() {
        // WHEN
        setFormUseCase.incBedroomCount()

        // THEN
        verify(exactly = 3) { mockFormRepository.getNonNullForm() }
        verify(exactly = 1) { mockFormRepository.setForm(CURRENT_STATE.copy(bedroomCount = 1)) }
    }

    @Test
    fun `do NOT add bedroom when increment count without rooms to spare`() {
        // GIVEN
        every { mockFormRepository.getNonNullForm() } returns CURRENT_STATE.copy(bedroomCount = 5)

        // WHEN
        setFormUseCase.incBedroomCount()

        // THEN
        verify(exactly = 1) { mockFormRepository.getNonNullForm() }
        verify(exactly = 0) { mockFormRepository.setForm(CURRENT_STATE.copy(bedroomCount = 1)) }
    }

    @Test
    fun `remove bedroom when decrement count with bedrooms to spare`() {
        // GIVEN
        every { mockFormRepository.getNonNullForm() } returns CURRENT_STATE.copy(bedroomCount = 1)

        // WHEN
        setFormUseCase.decBedroomCount()

        // THEN
        verify(exactly = 3) { mockFormRepository.getNonNullForm() }
        verify(exactly = 1) { mockFormRepository.setForm(CURRENT_STATE.copy(bedroomCount = 0)) }
    }

    @Test
    fun `do NOT remove bedroom when decrement count without bedrooms to spare`() {
        // WHEN
        setFormUseCase.decBedroomCount()

        // THEN
        verify(exactly = 1) { mockFormRepository.getNonNullForm() }
        verify(exactly = 0) { mockFormRepository.setForm(CURRENT_STATE.copy(bathroomCount = -1)) }
    }

    @Test
    fun `set form's description when update with different value`() {
        // WHEN
        setFormUseCase.updateDescription("Really great flat!", 6)

        // THEN
        verify(exactly = 2) { mockFormRepository.getNonNullForm() }
        verify(exactly = 1) {
            mockFormRepository.setForm(
                CURRENT_STATE.copy(description = "Really great flat!", descriptionCursor = 6)
            )
        }
    }

    @Test
    fun `do NOT set form's description when update with same value`() {
        // WHEN
        setFormUseCase.updateDescription("Great flat", 0)

        // THEN
        verify(exactly = 1) { mockFormRepository.getNonNullForm() }
        verify(exactly = 0) {
            mockFormRepository.setForm(
                CURRENT_STATE.copy(description = "Great flat", descriptionCursor = 0)
            )
        }
    }

    @Test
    fun `update repository when set picture position`() {
        // WHEN
        setFormUseCase.setPicturePosition(3)

        // THEN
        verify(exactly = 1) { mockFormRepository.setCurrentPicturePosition(3) }
    }

    @Test
    fun `update picture list when remove picture`() {
        // WHEN
        setFormUseCase.removePictureAt(0)

        // THEN
        verify(exactly = 2) { mockFormRepository.getNonNullForm() }
        verify(exactly = 1) {
            mockFormRepository.setForm(CURRENT_STATE.copy(pictureList = emptyList()))
        }
    }

    @Test
    fun `set picture list error to null when reset error`() {
        // WHEN
        setFormUseCase.resetPictureError()

        // THEN
        verify(exactly = 1) { mockFormRepository.getNonNullForm() }
        verify(exactly = 1) {
            mockFormRepository.setForm(CURRENT_STATE.copy(pictureListError = null))
        }
    }

    @Test
    fun `set new picture when init picture`() {
        // WHEN
        setFormUseCase.initPicture(mockUri, "Kitchen")

        // THEN
        val expectedPicture = CurrentPictureEntity(mockUri, "Kitchen", null, 0)
        verify(exactly = 1) { mockCurrentPictureRepository.setCurrentPicture(expectedPicture) }
    }

    @Test
    fun `update picture when set with existing value`() {
        // WHEN
        setFormUseCase.setPictureUri(mockUri)

        // THEN
        val expectedPicture = CURRENT_PICTURE.copy(uri = mockUri)
        verify(exactly = 1) { mockCurrentPictureRepository.getCurrentPicture() }
        verify(exactly = 1) { mockCurrentPictureRepository.setCurrentPicture(expectedPicture) }
    }

    @Test
    fun `init picture when set without existing value`() {
        // GIVEN
        every { mockCurrentPictureRepository.getCurrentPicture() } returns null

        // WHEN
        setFormUseCase.setPictureUri(mockUri)

        // THEN
        val expectedPicture = CurrentPictureEntity(mockUri, "", null, 0)
        verify(exactly = 1) { mockCurrentPictureRepository.getCurrentPicture() }
        verify(exactly = 1) { mockCurrentPictureRepository.setCurrentPicture(expectedPicture) }
    }

    @Test
    fun `set picture's description when update with different value`() {
        // WHEN
        setFormUseCase.updatePictureDescription("Bedroom", 2)

        // THEN
        verify(exactly = 1) { mockCurrentPictureRepository.getNonNullCurrentPicture() }
        verify(exactly = 1) {
            mockCurrentPictureRepository.setCurrentPicture(
                CURRENT_PICTURE.copy(description = "Bedroom", descriptionCursor = 2)
            )
        }
    }

    @Test
    fun `do NOT set picture's description when update with same value`() {
        // WHEN
        setFormUseCase.updatePictureDescription("Living room", 0)

        // THEN
        verify(exactly = 1) { mockCurrentPictureRepository.getNonNullCurrentPicture() }
        verify(exactly = 0) {
            mockCurrentPictureRepository.setCurrentPicture(
                CURRENT_PICTURE.copy(description = "Living room", descriptionCursor = 0)
            )
        }
    }

    @Test
    fun `call repository method when reset picture`() {
        // WHEN
        setFormUseCase.resetPicture()

        // THEN
        verify(exactly = 1) { mockCurrentPictureRepository.setCurrentPicture(null) }
    }

    @Test
    fun `add picture when save it out of the last position`() {
        // WHEN
        setFormUseCase.savePicture()

        // THEN
        val expectedPictureList = listOf(
            FormEntity.PictureEntity(mockUri, "Lounge"),
            FormEntity.PictureEntity(mockUri, "Living room")
        )
        verify(exactly = 2) { mockCurrentPictureRepository.getNonNullCurrentPicture() }
        verify(exactly = 2) { mockFormRepository.getNonNullForm() }
        verify(exactly = 1) { mockFormRepository.getCurrentPicturePosition() }
        verify(exactly = 1) {
            mockFormRepository.setForm(CURRENT_STATE.copy(pictureList = expectedPictureList))
        }
    }

    @Test
    fun `update picture when save it at an existing position`() {
        // GIVEN
        every { mockFormRepository.getCurrentPicturePosition() } returns 0

        // WHEN
        setFormUseCase.savePicture()

        // THEN
        val expectedPictureList = listOf(FormEntity.PictureEntity(mockUri, "Living room"))
        verify(exactly = 2) { mockCurrentPictureRepository.getNonNullCurrentPicture() }
        verify(exactly = 2) { mockFormRepository.getNonNullForm() }
        verify(exactly = 1) { mockFormRepository.getCurrentPicturePosition() }
        verify(exactly = 1) {
            mockFormRepository.setForm(CURRENT_STATE.copy(pictureList = expectedPictureList))
        }
    }

    @Test
    fun `set form's street name when update with different value`() {
        // WHEN
        setFormUseCase.updateStreetName("Main street", 0)

        // THEN
        verify(exactly = 2) { mockFormRepository.getNonNullForm() }
        verify(exactly = 1) {
            mockFormRepository.setForm(
                CURRENT_STATE.copy(streetName = "Main street", streetNameCursor = 0)
            )
        }
    }

    @Test
    fun `do NOT set form's street name when update with same value`() {
        // WHEN
        setFormUseCase.updateStreetName("740 Park Avenue", 0)

        // THEN
        verify(exactly = 1) { mockFormRepository.getNonNullForm() }
        verify(exactly = 0) {
            mockFormRepository.setForm(
                CURRENT_STATE.copy(streetName = "740 Park Avenue", streetNameCursor = 0)
            )
        }
    }

    @Test
    fun `set form's additional address when update with different value`() {
        // WHEN
        setFormUseCase.updateAdditionalAddressInfo("First floor", 5)

        // THEN
        verify(exactly = 2) { mockFormRepository.getNonNullForm() }
        verify(exactly = 1) {
            mockFormRepository.setForm(
                CURRENT_STATE.copy(
                    additionalAddressInfo = "First floor",
                    additionalAddressInfoCursor = 5
                )
            )
        }
    }

    @Test
    fun `do NOT set form's additional address when update with same value`() {
        // WHEN
        setFormUseCase.updateAdditionalAddressInfo("Apt 6/7A", 0)

        // THEN
        verify(exactly = 1) { mockFormRepository.getNonNullForm() }
        verify(exactly = 0) {
            mockFormRepository.setForm(
                CURRENT_STATE.copy(
                    additionalAddressInfo = "Apt 6/7A",
                    additionalAddressInfoCursor = 0
                )
            )
        }
    }

    @Test
    fun `set form's city when update with different value`() {
        // WHEN
        setFormUseCase.updateCity("New Jersey", 4)

        // THEN
        verify(exactly = 2) { mockFormRepository.getNonNullForm() }
        verify(exactly = 1) {
            mockFormRepository.setForm(CURRENT_STATE.copy(city = "New Jersey", cityCursor = 4))
        }
    }

    @Test
    fun `do NOT set form's city when update with same value`() {
        // WHEN
        setFormUseCase.updateCity("New York", 0)

        // THEN
        verify(exactly = 1) { mockFormRepository.getNonNullForm() }
        verify(exactly = 0) {
            mockFormRepository.setForm(CURRENT_STATE.copy(city = "New York", cityCursor = 0))
        }
    }

    @Test
    fun `set form's state when update with different value`() {
        // WHEN
        setFormUseCase.updateState("ca", 2)

        // THEN
        verify(exactly = 2) { mockFormRepository.getNonNullForm() }
        verify(exactly = 1) {
            mockFormRepository.setForm(CURRENT_STATE.copy(state = "CA", stateCursor = 2))
        }
    }

    @Test
    fun `do NOT set form's state when update with same value`() {
        // WHEN
        setFormUseCase.updateState("NY", 2)

        // THEN
        verify(exactly = 1) { mockFormRepository.getNonNullForm() }
        verify(exactly = 0) {
            mockFormRepository.setForm(CURRENT_STATE.copy(state = "NY", stateCursor = 2))
        }
    }

    @Test
    fun `set form's zipcode when update with different value`() {
        // WHEN
        setFormUseCase.updateZipcode("60501", 0)

        // THEN
        verify(exactly = 2) { mockFormRepository.getNonNullForm() }
        verify(exactly = 1) {
            mockFormRepository.setForm(CURRENT_STATE.copy(zipcode = "60501", zipcodeCursor = 0))
        }
    }

    @Test
    fun `do NOT set form's zipcode when update with same value`() {
        // WHEN
        setFormUseCase.updateZipcode("10021", 0)

        // THEN
        verify(exactly = 1) { mockFormRepository.getNonNullForm() }
        verify(exactly = 0) {
            mockFormRepository.setForm(CURRENT_STATE.copy(zipcode = "10021", zipcodeCursor = 0))
        }
    }

    @Test
    fun `set form's country when update with different value`() {
        // WHEN
        setFormUseCase.updateCountry("60501", 2)

        // THEN
        verify(exactly = 2) { mockFormRepository.getNonNullForm() }
        verify(exactly = 1) {
            mockFormRepository.setForm(CURRENT_STATE.copy(country = "60501", countryCursor = 2))
        }
    }

    @Test
    fun `do NOT set form's country when update with same value`() {
        // WHEN
        setFormUseCase.updateCountry("United States", 0)

        // THEN
        verify(exactly = 1) { mockFormRepository.getNonNullForm() }
        verify(exactly = 0) {
            mockFormRepository.setForm(
                CURRENT_STATE.copy(
                    country = "United States",
                    countryCursor = 0
                )
            )
        }
    }

    @Test
    fun `add poi when update with being checked`() {
        // WHEN
        setFormUseCase.updatePoi(labelId = 5, isChecked = true)

        // THEN
        val expectedPoiList = listOf(4, 8, 13, 5)
        verify(exactly = 2) { mockFormRepository.getNonNullForm() }
        verify(exactly = 1) {
            mockFormRepository.setForm(CURRENT_STATE.copy(pointsOfInterests = expectedPoiList))
        }
    }

    @Test
    fun `remove poi when update with being unchecked`() {
        // WHEN
        setFormUseCase.updatePoi(labelId = 8, isChecked = false)

        // THEN
        val expectedPoiList = listOf(4, 13)
        verify(exactly = 2) { mockFormRepository.getNonNullForm() }
        verify(exactly = 1) {
            mockFormRepository.setForm(CURRENT_STATE.copy(pointsOfInterests = expectedPoiList))
        }
    }

    @Test
    fun `set form's agent when update name`() {
        // WHEN
        setFormUseCase.updateAgent("Agent Z")

        // THEN
        verify(exactly = 1) { mockFormRepository.getNonNullForm() }
        verify(exactly = 1) { mockFormRepository.setForm(CURRENT_STATE.copy(agentName = "Agent Z")) }
    }

    @Test
    fun `set form's entry date when update value`() {
        // WHEN
        setFormUseCase.updateMarketEntryDate("22/02/2022")

        // THEN
        verify(exactly = 1) { mockFormRepository.getNonNullForm() }
        verify(exactly = 1) {
            mockFormRepository.setForm(CURRENT_STATE.copy(marketEntryDate = "22/02/2022"))
        }
    }

    @Test
    fun `set form's sale date when update value`() {
        // WHEN
        setFormUseCase.updateSaleDate("23/02/2022")

        // THEN
        verify(exactly = 1) { mockFormRepository.getNonNullForm() }
        verify(exactly = 1) {
            mockFormRepository.setForm(CURRENT_STATE.copy(saleDate = "23/02/2022"))
        }
    }

    @Test
    fun `enable form's availability when update value to true`() {
        // WHEN
        setFormUseCase.updateAvailability(true)

        // THEN
        verify(exactly = 1) { mockFormRepository.getNonNullForm() }
        verify(exactly = 1) {
            mockFormRepository.setForm(CURRENT_STATE.copy(isAvailableForSale = true))
        }
    }

    @Test
    fun `disable form's availability when update value to false`() {
        // WHEN
        setFormUseCase.updateAvailability(false)

        // THEN
        verify(exactly = 1) { mockFormRepository.getNonNullForm() }
        verify(exactly = 1) {
            mockFormRepository.setForm(CURRENT_STATE.copy(isAvailableForSale = false))
        }
    }
}