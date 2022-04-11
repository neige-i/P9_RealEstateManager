package com.openclassrooms.realestatemanager.domain.real_estate

import android.content.Context
import android.net.Uri
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.form.FormEntity
import com.openclassrooms.realestatemanager.data.form.FormRepository
import com.openclassrooms.realestatemanager.data.real_estate.RealEstateEntity
import com.openclassrooms.realestatemanager.data.real_estate.RealEstateRepository
import com.openclassrooms.realestatemanager.util.TestCoroutineRule
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class SaveRealEstateUseCaseTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @MockK
    private lateinit var mockFormRepository: FormRepository

    @MockK
    private lateinit var mockRealEstateRepository: RealEstateRepository

    @MockK
    private lateinit var mockAgentRepository: AgentRepository

    @MockK
    private lateinit var mockContext: Context

    private lateinit var saveRealEstateUseCase: SaveRealEstateUseCase

    companion object {
        // region IN
        private val DEFAULT_URI = mockk<Uri>()
        private const val DEFAULT_AGENT_NAME = "Agent K"
        private const val UNKNOWN_AGENT_NAME = "unknown"
        private val DEFAULT_FORM = FormEntity(
            type = "Flat",
            typeError = null,
            price = "98324.67",
            priceCursor = 4,
            area = "78",
            areaCursor = 2,
            totalRoomCount = 3,
            bathroomCount = 1,
            bedroomCount = 1,
            description = "Really nice",
            descriptionCursor = 5,
            pictureList = listOf(FormEntity.PictureEntity(DEFAULT_URI, "Main room")),
            pictureListError = null,
            streetName = "740 Park Avenue",
            streetNameError = null,
            streetNameCursor = 5,
            additionalAddressInfo = "Apt 6/7A",
            additionalAddressInfoCursor = 1,
            city = "New York",
            cityError = null,
            cityCursor = 6,
            state = "NY",
            stateError = null,
            stateCursor = 2,
            zipcode = "10021",
            zipcodeError = null,
            zipcodeCursor = 1,
            country = "United States",
            countryError = null,
            countryCursor = 9,
            pointsOfInterests = listOf(R.string.label_poi_bar, R.string.label_poi_park),
            agentName = DEFAULT_AGENT_NAME,
            marketEntryDate = "21/02/2022",
            marketEntryDateError = null,
            saleDate = "22/02/2022",
            saleDateError = null,
            isAvailableForSale = false
        )
        // endregion IN

        // region OUT
        private val DEFAULT_ESTATE = RealEstateEntity(
            type = "FLAT",
            price = 98324.67,
            area = 78,
            totalRoomCount = 3,
            bathroomCount = 1,
            bedroomCount = 1,
            description = "Really nice",
            pictureList = listOf("Uri String"),
            streetName = "740 Park Avenue",
            additionalAddressInfo = "Apt 6/7A",
            city = "New York",
            state = "NY",
            zipcode = "10021",
            country = "United States",
            pointsOfInterests = listOf("BAR", "PARK"),
            agentId = "1",
            marketEntryDate = "21/02/2022",
            saleDate = "22/02/2022",
        )
        // endregion OUT
    }

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        every { mockFormRepository.getForm() } returns DEFAULT_FORM
        coJustRun { mockRealEstateRepository.addEstate(any()) }
        every { mockContext.getString(any()) } returns "Flat"
        every { DEFAULT_URI.toString() } returns "Uri String"
        every { mockAgentRepository.getAgentByName(DEFAULT_AGENT_NAME) }
            .returns(AgentEntity("1", DEFAULT_AGENT_NAME))
        every { mockAgentRepository.getAgentByName(UNKNOWN_AGENT_NAME) } returns null

        saveRealEstateUseCase = SaveRealEstateUseCase(
            mockFormRepository,
            mockRealEstateRepository,
            mockAgentRepository,
            mockContext
        )
    }

    @After
    fun tearDown() {
        verify(exactly = 1) { mockFormRepository.getForm() }
        verify(exactly = 1) { mockContext.getString(any()) }
        confirmVerified(
            mockFormRepository,
            mockRealEstateRepository,
            mockAgentRepository,
            mockContext
        )
    }

    @Test
    fun `add real estate to DB when create it`() = runTest {
        // WHEN
        saveRealEstateUseCase.invoke()

        // THEN
        coVerify(exactly = 1) { mockRealEstateRepository.addEstate(DEFAULT_ESTATE) }
        verify(exactly = 1) { mockAgentRepository.getAgentByName(DEFAULT_AGENT_NAME) }
    }

    @Test
    fun `add real estate to DB when create entity with unknown agent`() = runTest {
        // GIVEN
        every { mockFormRepository.getForm() } returns DEFAULT_FORM.copy(agentName = UNKNOWN_AGENT_NAME)

        // WHEN
        saveRealEstateUseCase.invoke()

        // THEN
        coVerify(exactly = 1) {
            mockRealEstateRepository.addEstate(DEFAULT_ESTATE.copy(agentId = null))
        }
        verify(exactly = 1) { mockAgentRepository.getAgentByName(UNKNOWN_AGENT_NAME) }
    }

    @Test
    fun `add real estate to DB when create entity with empty sale date`() = runTest {
        // GIVEN
        every { mockFormRepository.getForm() } returns DEFAULT_FORM.copy(saleDate = "")

        // WHEN
        saveRealEstateUseCase.invoke()

        // THEN
        coVerify(exactly = 1) {
            mockRealEstateRepository.addEstate(DEFAULT_ESTATE.copy(saleDate = null))
        }
        verify(exactly = 1) { mockAgentRepository.getAgentByName(DEFAULT_AGENT_NAME) }
    }
}