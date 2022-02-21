package com.openclassrooms.realestatemanager.domain.real_estate

import android.content.Context
import android.net.Uri
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.agent.AgentEntity
import com.openclassrooms.realestatemanager.data.agent.AgentRepository
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
class CreateRealEstateUseCaseTest {

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

    private lateinit var createRealEstateUseCase: CreateRealEstateUseCase

    companion object {
        // IN
        private val MOCK_URI = mockk<Uri>()
        private val NOMINAL_FORM = FormRepository.DEFAULT_FORM.copy(
            type = "Condo",
            price = "98324.67",
            area = "78",
            totalRoomCount = 3,
            bathroomCount = 1,
            bedroomCount = 1,
            description = "Really nice",
            pictureList = listOf(FormEntity.PictureEntity(MOCK_URI, "Main room")),
            streetName = "740 Park Avenue",
            additionalAddressInfo = "Apt 6/7A",
            city = "New York",
            state = "NY",
            zipcode = "10021",
            country = "United States",
            pointsOfInterests = listOf(R.string.label_poi_bar, R.string.label_poi_park),
            agentName = "Agent K",
            marketEntryDate = "21/02/2022",
            saleDate = "22/02/2022",
        )

        // OUT
        private val NOMINAL_ESTATE = RealEstateEntity(
            type = "FLAT",
            price = 98324.67,
            area = 78,
            totalRoomCount = 3,
            bathroomCount = 1,
            bedroomCount = 1,
            description = "Really nice",
            pictureList = listOf("uri string"),
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
    }

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        every { mockFormRepository.getNonNullForm() } returns NOMINAL_FORM
        coJustRun { mockRealEstateRepository.createRealEstate(any()) }
        every { mockContext.getString(any()) } returns "Condo"
        every { MOCK_URI.toString() } returns "uri string"
        every { mockAgentRepository.getAgentByName("Agent K") } returns AgentEntity("1", "Agent K")
        every { mockAgentRepository.getAgentByName("unknown") } returns null

        createRealEstateUseCase = CreateRealEstateUseCase(
            mockFormRepository,
            mockRealEstateRepository,
            mockAgentRepository,
            mockContext
        )
    }

    @After
    fun tearDown() {
        verify(exactly = 1) { mockFormRepository.getNonNullForm() }
        verify(exactly = 1) { mockContext.getString(any()) }
        confirmVerified(
            mockFormRepository,
            mockRealEstateRepository,
            mockAgentRepository,
            mockContext
        )
    }

    @Test
    fun `add real estate to DB when create nominal entity`() = runTest {
        // WHEN
        createRealEstateUseCase.invoke()

        // THEN
        coVerify(exactly = 1) { mockRealEstateRepository.createRealEstate(NOMINAL_ESTATE) }
        verify(exactly = 1) { mockAgentRepository.getAgentByName("Agent K") }
    }

    @Test
    fun `add real estate to DB when create entity with unknown agent`() = runTest {
        // GIVEN
        every { mockFormRepository.getNonNullForm() } returns NOMINAL_FORM.copy(agentName = "unknown")

        // WHEN
        createRealEstateUseCase.invoke()

        // THEN
        coVerify(exactly = 1) {
            mockRealEstateRepository.createRealEstate(NOMINAL_ESTATE.copy(agentId = null))
        }
        verify(exactly = 1) { mockAgentRepository.getAgentByName("unknown") }
    }

    @Test
    fun `add real estate to DB when create entity with empty date`() = runTest {
        // GIVEN
        every { mockFormRepository.getNonNullForm() } returns NOMINAL_FORM.copy(saleDate = "")

        // WHEN
        createRealEstateUseCase.invoke()

        // THEN
        coVerify(exactly = 1) {
            mockRealEstateRepository.createRealEstate(NOMINAL_ESTATE.copy(saleDate = null))
        }
        verify(exactly = 1) { mockAgentRepository.getAgentByName("Agent K") }
    }
}