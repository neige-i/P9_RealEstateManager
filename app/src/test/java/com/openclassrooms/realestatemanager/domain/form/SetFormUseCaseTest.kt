package com.openclassrooms.realestatemanager.domain.form

import android.net.Uri
import com.openclassrooms.realestatemanager.data.form.*
import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.junit.After
import org.junit.Before
import org.junit.Test

class SetFormUseCaseTest {

    @MockK
    private lateinit var mockFormRepository: FormRepository

    @MockK
    private lateinit var mockCurrentPictureRepository: CurrentPictureRepository

    @MockK
    private lateinit var mockActionRepository: ActionRepository

    private lateinit var setFormUseCase: SetFormUseCase

    companion object {
        // region IN
        private val MOCK_URI = mockk<Uri>()
        private val CURRENT_FORM = FormRepository.DEFAULT_FORM.copy(
            type = "Flat",
            price = "99000.99",
            area = "42",
            totalRoomCount = 7,
            bathroomCount = 1,
            bedroomCount = 4,
            description = "Great flat",
            pictureList = listOf(FormEntity.PictureEntity(MOCK_URI, "Lounge")),
            pictureListError = "ERROR",
            streetName = "740 Park Avenue",
            additionalAddressInfo = "Apt 6/7A",
            city = "New York",
            cityError = "ERROR",
            state = "NY",
            zipcode = "10021",
            country = "United States",
            pointsOfInterests = listOf(4, 8, 13)
        )
        // endregion IN

        // region OUT
        private val DEFAULT_PICTURE = FormEntity.PictureEntity(
            uri = MOCK_URI,
            description = "Living room",
        )
    }

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        every { mockFormRepository.getForm() } returns CURRENT_FORM
        every { mockFormRepository.getCurrentPicturePosition() } returns 1
        justRun { mockFormRepository.initForm(any()) }
        justRun { mockFormRepository.resetAllErrors() }
        justRun { mockFormRepository.resetForm() }
        justRun { mockFormRepository.setType(any()) }
        justRun { mockFormRepository.setPrice(any(), any()) }
        justRun { mockFormRepository.setArea(any(), any()) }
        justRun { mockFormRepository.incTotalRoom() }
        justRun { mockFormRepository.decTotalRoom() }
        justRun { mockFormRepository.incBathroom() }
        justRun { mockFormRepository.decBathroom() }
        justRun { mockFormRepository.incBedroom() }
        justRun { mockFormRepository.decBedroom() }
        justRun { mockFormRepository.setDescription(any(), any()) }
        justRun { mockFormRepository.setCurrentPicturePosition(any()) }
        justRun { mockFormRepository.deletePictureAt(any()) }
        justRun { mockFormRepository.setPictureListError(any()) }
        justRun { mockFormRepository.addPicture(any()) }
        justRun { mockFormRepository.setPictureAt(any(), any()) }
        justRun { mockFormRepository.setStreetName(any(), any()) }
        justRun { mockFormRepository.setAdditionalAddress(any(), any()) }
        justRun { mockFormRepository.setCity(any(), any()) }
        justRun { mockFormRepository.setState(any(), any()) }
        justRun { mockFormRepository.setZipcode(any(), any()) }
        justRun { mockFormRepository.setCountry(any(), any()) }
        justRun { mockFormRepository.addPoi(any()) }
        justRun { mockFormRepository.removePoi(any()) }
        justRun { mockFormRepository.setAgentName(any()) }
        justRun { mockFormRepository.setEntryDate(any()) }
        justRun { mockFormRepository.setSaleDate(any()) }
        justRun { mockFormRepository.setAvailability(any()) }
        every { mockCurrentPictureRepository.getCurrentPicture() } returns CurrentPictureEntity(
            uri = MOCK_URI,
            description = "Living room",
            descriptionError = null,
            descriptionCursor = 0
        )
        justRun { mockCurrentPictureRepository.initPicture(any(), any()) }
        justRun { mockCurrentPictureRepository.setUri(any()) }
        justRun { mockCurrentPictureRepository.setDescription(any(), any()) }
        justRun { mockCurrentPictureRepository.reset() }
        justRun { mockActionRepository.requestPictureOpening() }

        setFormUseCase = SetFormUseCase(
            mockFormRepository,
            mockCurrentPictureRepository,
            mockActionRepository
        )
    }

    @After
    fun tearDown() {
        confirmVerified(mockFormRepository, mockCurrentPictureRepository, mockActionRepository)
    }

    @Test
    fun `update initial & reset errors when init ADD form`() {
        // WHEN
        setFormUseCase.initAddForm()

        // THEN
        verify(exactly = 1) { mockFormRepository.initForm(FormRepository.DEFAULT_FORM) }
        verify(exactly = 1) { mockFormRepository.resetAllErrors() }
    }

    @Test
    fun `reset form when call reset`() {
        // WHEN
        setFormUseCase.reset()

        // THEN
        verify(exactly = 1) { mockFormRepository.resetForm() }
    }

    @Test
    fun `set form's type when update value`() {
        // WHEN
        setFormUseCase.updateType("new type")

        // THEN
        verify(exactly = 1) { mockFormRepository.setType("new type") }
    }

    @Test
    fun `set form's price when update value`() {
        // WHEN
        setFormUseCase.updatePrice("155000", 0)

        // THEN
        verify(exactly = 1) { mockFormRepository.setPrice("155000", 0) }
    }

    @Test
    fun `set form's area when update value`() {
        // WHEN
        setFormUseCase.updateArea("50", 2)

        // THEN
        verify(exactly = 1) { mockFormRepository.setArea("50", 2) }
    }

    @Test
    fun `add total room when increment count`() {
        // WHEN
        setFormUseCase.incTotalRoomCount()

        // THEN
        verify(exactly = 1) { mockFormRepository.incTotalRoom() }
    }

    @Test
    fun `remove total room when decrement count with rooms to spare`() {
        // WHEN
        setFormUseCase.decTotalRoomCount()

        // THEN
        verify(exactly = 1) { mockFormRepository.getForm() }
        verify(exactly = 1) { mockFormRepository.decTotalRoom() }
    }

    @Test
    fun `do NOT remove total room when decrement count without rooms to spare`() {
        // GIVEN
        every { mockFormRepository.getForm() } returns CURRENT_FORM.copy(totalRoomCount = 5)

        // WHEN
        setFormUseCase.decTotalRoomCount()

        // THEN
        verify(exactly = 1) { mockFormRepository.getForm() }
        verify(exactly = 0) { mockFormRepository.decTotalRoom() }
    }

    @Test
    fun `add bathroom when increment count with rooms to spare`() {
        // WHEN
        setFormUseCase.incBathroomCount()

        // THEN
        verify(exactly = 1) { mockFormRepository.getForm() }
        verify(exactly = 1) { mockFormRepository.incBathroom() }
    }

    @Test
    fun `do NOT add bathroom when increment count without rooms to spare`() {
        // GIVEN
        every { mockFormRepository.getForm() } returns CURRENT_FORM.copy(totalRoomCount = 5)

        // WHEN
        setFormUseCase.incBathroomCount()

        // THEN
        verify(exactly = 1) { mockFormRepository.getForm() }
        verify(exactly = 0) { mockFormRepository.setForm(CURRENT_FORM.copy(bathroomCount = 1)) }
    }

    @Test
    fun `remove bathroom when decrement count with bathrooms to spare`() {
        // WHEN
        setFormUseCase.decBathroomCount()

        // THEN
        verify(exactly = 1) { mockFormRepository.getForm() }
        verify(exactly = 1) { mockFormRepository.decBathroom() }
    }

    @Test
    fun `do NOT remove bathroom when decrement count without bathrooms to spare`() {
        // GIVEN
        every { mockFormRepository.getForm() } returns CURRENT_FORM.copy(bathroomCount = 0)

        // WHEN
        setFormUseCase.decBathroomCount()

        // THEN
        verify(exactly = 1) { mockFormRepository.getForm() }
        verify(exactly = 0) { mockFormRepository.decBathroom() }
    }

    @Test
    fun `add bedroom when increment count with rooms to spare`() {
        // WHEN
        setFormUseCase.incBedroomCount()

        // THEN
        verify(exactly = 1) { mockFormRepository.getForm() }
        verify(exactly = 1) { mockFormRepository.incBedroom() }
    }

    @Test
    fun `do NOT add bedroom when increment count without rooms to spare`() {
        // GIVEN
        every { mockFormRepository.getForm() } returns CURRENT_FORM.copy(totalRoomCount = 5)

        // WHEN
        setFormUseCase.incBedroomCount()

        // THEN
        verify(exactly = 1) { mockFormRepository.getForm() }
        verify(exactly = 0) { mockFormRepository.incBedroom() }
    }

    @Test
    fun `remove bedroom when decrement count with bedrooms to spare`() {
        // WHEN
        setFormUseCase.decBedroomCount()

        // THEN
        verify(exactly = 1) { mockFormRepository.getForm() }
        verify(exactly = 1) { mockFormRepository.decBedroom() }
    }

    @Test
    fun `do NOT remove bedroom when decrement count without bedrooms to spare`() {
        // GIVEN
        every { mockFormRepository.getForm() } returns CURRENT_FORM.copy(bedroomCount = 0)

        // WHEN
        setFormUseCase.decBedroomCount()

        // THEN
        verify(exactly = 1) { mockFormRepository.getForm() }
        verify(exactly = 0) { mockFormRepository.decBedroom() }
    }

    @Test
    fun `set form's description when update value`() {
        // WHEN
        setFormUseCase.updateDescription("Really great flat!", 6)

        // THEN
        verify(exactly = 1) { mockFormRepository.setDescription("Really great flat!", 6) }
    }

    @Test
    fun `set picture position when update value`() {
        // WHEN
        setFormUseCase.updatePicturePosition(3)

        // THEN
        verify(exactly = 1) { mockFormRepository.setCurrentPicturePosition(3) }
    }

    @Test
    fun `delete picture when remove picture`() {
        // WHEN
        setFormUseCase.removePictureAt(6)

        // THEN
        verify(exactly = 1) { mockFormRepository.deletePictureAt(6) }
    }

    @Test
    fun `set picture list error to null when reset picture error`() {
        // WHEN
        setFormUseCase.resetPictureError()

        // THEN
        verify(exactly = 1) { mockFormRepository.setPictureListError(null) }
    }

    @Test
    fun `init picture & request opening it when set picture`() {
        // WHEN
        setFormUseCase.setPicture(MOCK_URI, "Kitchen")

        // THEN
        verify(exactly = 1) { mockCurrentPictureRepository.initPicture(MOCK_URI, "Kitchen") }
        verify(exactly = 1) { mockActionRepository.requestPictureOpening() }
    }

    @Test
    fun `update picture Uri when set its Uri while picture is already initialized`() {
        // WHEN
        setFormUseCase.setPictureUri(MOCK_URI)

        // THEN
        verify(exactly = 1) { mockCurrentPictureRepository.getCurrentPicture() }
        verify(exactly = 1) { mockCurrentPictureRepository.setUri(MOCK_URI) }
    }

    @Test
    fun `set a new picture when set its Uri while picture is not initialized`() {
        // GIVEN
        every { mockCurrentPictureRepository.getCurrentPicture() } returns null

        // WHEN
        setFormUseCase.setPictureUri(MOCK_URI)

        // THEN
        verify(exactly = 1) { mockCurrentPictureRepository.getCurrentPicture() }
        verify(exactly = 1) { mockCurrentPictureRepository.initPicture(MOCK_URI, "") }
        verify(exactly = 1) { mockActionRepository.requestPictureOpening() }
    }

    @Test
    fun `set picture's description when update value`() {
        // WHEN
        setFormUseCase.updatePictureDescription("Bedroom", 2)

        // THEN
        verify(exactly = 1) { mockCurrentPictureRepository.setDescription("Bedroom", 2) }
    }

    @Test
    fun `reset picture when call reset`() {
        // WHEN
        setFormUseCase.resetPicture()

        // THEN
        verify(exactly = 1) { mockCurrentPictureRepository.reset() }
    }

    @Test
    fun `add picture when save it out of the last position`() {
        // WHEN
        setFormUseCase.savePicture()

        // THEN
        verify(exactly = 1) { mockCurrentPictureRepository.getCurrentPicture() }
        verify(exactly = 1) { mockFormRepository.getCurrentPicturePosition() }
        verify(exactly = 1) { mockFormRepository.getForm() }
        verify(exactly = 1) { mockFormRepository.addPicture(DEFAULT_PICTURE) }
    }

    @Test
    fun `update picture when save it at an existing position`() {
        // GIVEN
        every { mockFormRepository.getCurrentPicturePosition() } returns 0

        // WHEN
        setFormUseCase.savePicture()

        // THEN
        verify(exactly = 1) { mockCurrentPictureRepository.getCurrentPicture() }
        verify(exactly = 1) { mockFormRepository.getCurrentPicturePosition() }
        verify(exactly = 1) { mockFormRepository.getForm() }
        verify(exactly = 1) { mockFormRepository.setPictureAt(0, DEFAULT_PICTURE) }
    }

    @Test
    fun `do nothing when save null picture`() {
        // GIVEN
        every { mockCurrentPictureRepository.getCurrentPicture() } returns null

        // WHEN
        setFormUseCase.savePicture()

        // THEN
        verify(exactly = 1) { mockCurrentPictureRepository.getCurrentPicture() }
        verify(exactly = 0) { mockFormRepository.addPicture(any()) }
        verify(exactly = 0) { mockFormRepository.setPictureAt(any(), any()) }
    }

    @Test
    fun `set form's street name when update value`() {
        // WHEN
        setFormUseCase.updateStreetName("Main street", 0)

        // THEN
        verify(exactly = 1) { mockFormRepository.setStreetName("Main street", 0) }
    }

    @Test
    fun `set form's additional address when update value`() {
        // WHEN
        setFormUseCase.updateAdditionalAddressInfo("First floor", 5)

        // THEN
        verify(exactly = 1) { mockFormRepository.setAdditionalAddress("First floor", 5) }
    }

    @Test
    fun `set form's city when update value`() {
        // WHEN
        setFormUseCase.updateCity("New Jersey", 4)

        // THEN
        verify(exactly = 1) { mockFormRepository.setCity("New Jersey", 4) }
    }

    @Test
    fun `set form's state when update value`() {
        // WHEN
        setFormUseCase.updateState("ca", 2)

        // THEN
        verify(exactly = 1) { mockFormRepository.setState("CA", 2) }
    }

    @Test
    fun `set form's zipcode when update value`() {
        // WHEN
        setFormUseCase.updateZipcode("60501", 0)

        // THEN
        verify(exactly = 1) { mockFormRepository.setZipcode("60501", 0) }
    }

    @Test
    fun `set form's country when update value`() {
        // WHEN
        setFormUseCase.updateCountry("Canada", 2)

        // THEN
        verify(exactly = 1) { mockFormRepository.setCountry("Canada", 2) }
    }

    @Test
    fun `add poi when update with being checked`() {
        // WHEN
        setFormUseCase.updatePoi(labelId = 5, isChecked = true)

        // THEN
        verify(exactly = 1) { mockFormRepository.addPoi(5) }
    }

    @Test
    fun `remove poi when update with being unchecked`() {
        // WHEN
        setFormUseCase.updatePoi(labelId = 8, isChecked = false)

        // THEN
        verify(exactly = 1) { mockFormRepository.removePoi(8) }
    }

    @Test
    fun `set form's agent when update value`() {
        // WHEN
        setFormUseCase.updateAgent("Agent Z")

        // THEN
        verify(exactly = 1) { mockFormRepository.setAgentName("Agent Z") }
    }

    @Test
    fun `set form's entry date when update value`() {
        // WHEN
        setFormUseCase.updateMarketEntryDate("22/02/2022")

        // THEN
        verify(exactly = 1) { mockFormRepository.setEntryDate("22/02/2022") }
    }

    @Test
    fun `set form's sale date when update value`() {
        // WHEN
        setFormUseCase.updateSaleDate("23/02/2022")

        // THEN
        verify(exactly = 1) { mockFormRepository.setSaleDate("23/02/2022") }
    }

    @Test
    fun `enable form's availability when update value to true`() {
        // WHEN
        setFormUseCase.updateAvailability(true)

        // THEN
        verify(exactly = 1) { mockFormRepository.setAvailability(true) }
    }

    @Test
    fun `disable form's availability when update value to false`() {
        // WHEN
        setFormUseCase.updateAvailability(false)

        // THEN
        verify(exactly = 1) { mockFormRepository.setAvailability(false) }
        verify(exactly = 1) { mockFormRepository.setSaleDate("") }
    }
}