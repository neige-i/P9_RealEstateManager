package com.openclassrooms.realestatemanager.ui.form.sale

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.UtilsRepository
import com.openclassrooms.realestatemanager.data.form.FormRepository
import com.openclassrooms.realestatemanager.domain.form.GetFormUseCase
import com.openclassrooms.realestatemanager.domain.form.SetFormUseCase
import com.openclassrooms.realestatemanager.ui.util.CoroutineProvider
import com.openclassrooms.realestatemanager.util.TestCoroutineRule
import com.openclassrooms.realestatemanager.util.TestLifecycle.getValueForTesting
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.Clock
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset

@ExperimentalCoroutinesApi
class EditSaleViewModelTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var mockGetFormUseCase: GetFormUseCase

    @MockK
    private lateinit var mockSetFormUseCase: SetFormUseCase

    @MockK
    private lateinit var mockGetAgentListUseCase: GetAgentListUseCase

    private val clockMarch1st2022 = Clock.fixed(
        LocalDate.of(2022, 3, 1).atStartOfDay().toInstant(ZoneOffset.UTC),
        ZoneOffset.UTC
    )

    private val fixedZoneId = ZoneId.of("UTC")

    @MockK
    private lateinit var mockUtilsRepository: UtilsRepository

    @MockK
    private lateinit var mockCoroutineProvider: CoroutineProvider

    @MockK
    private lateinit var mockApplication: Application

    private lateinit var editSaleViewModel: EditSaleViewModel

    companion object {
        // region IN
        private val NOMINAL_FORM = FormRepository.DEFAULT_FORM.copy(
            agentName = "Agent Z",
            marketEntryDate = "",
            marketEntryDateError = "ERR",
            saleDate = "02/03/2022",
            saleDateError = null,
            isAvailableForSale = false,
        )
        private val FORM_FLOW = MutableStateFlow(NOMINAL_FORM)
        private val DEFAULT_AGENTS = listOf(
            AgentEntity("1", "Agent K"),
            AgentEntity("2", "Agent J"),
            AgentEntity("3", "Agent Z"),
        )
        // endregion IN

        // region OUT
        private val DEFAULT_VIEW_STATE = SaleViewState(
            agentEntries = listOf(
                "Agent K",
                "Agent J",
                "Agent Z",
            ),
            selectedAgentName = "Agent Z",
            marketEntryDate = "",
            marketEntryDateError = "ERR",
            saleDate = "02/03/2022",
            saleDateError = null,
            isAvailableForSale = false,
        )
        // endregion OUT
    }

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        every { mockGetFormUseCase.getFormFlow() } returns FORM_FLOW
        every { mockGetAgentListUseCase.invoke() } returns flowOf(DEFAULT_AGENTS)
        every { mockCoroutineProvider.getIoDispatcher() } returns testCoroutineRule.testCoroutineDispatcher
        justRun { mockSetFormUseCase.updateAgent(any()) }
        justRun { mockSetFormUseCase.updateMarketEntryDate(any()) }
        justRun { mockSetFormUseCase.updateSaleDate(any()) }
        justRun { mockSetFormUseCase.updateAvailability(any()) }
        every { mockApplication.getString(R.string.picker_title_market_entry) } returns "Entry date"
        every { mockApplication.getString(R.string.picker_title_sale) } returns "Sale date"
        every { mockUtilsRepository.stringToDate("02/03/2022") } returns LocalDate.of(2022, 3, 2)

        editSaleViewModel = EditSaleViewModel(
            mockGetFormUseCase,
            mockSetFormUseCase,
            mockGetAgentListUseCase,
            clockMarch1st2022,
            fixedZoneId,
            mockCoroutineProvider,
            mockApplication,
        )
    }

    @After
    fun tearDown() {
        verify(exactly = 1) { mockGetFormUseCase.getFormFlow() }
        verify(exactly = 1) { mockGetAgentListUseCase.invoke() }
        verify(exactly = 1) { mockCoroutineProvider.getIoDispatcher() }
        confirmVerified(mockGetFormUseCase, mockSetFormUseCase, mockCoroutineProvider)
    }

    @Test
    fun `return default view state when get nominal form & default agents`() {
        // GIVEN
        FORM_FLOW.value = NOMINAL_FORM

        // WHEN
        val viewState = getValueForTesting(editSaleViewModel.viewStateLiveData)

        // THEN
        assertEquals(DEFAULT_VIEW_STATE, viewState)
    }

    @Test
    fun `return view state without selected agent when get agent name is unknown`() {
        // GIVEN
        FORM_FLOW.update { it.copy(agentName = "john doe") }

        // WHEN
        val viewState = getValueForTesting(editSaleViewModel.viewStateLiveData)

        // THEN
        assertEquals(DEFAULT_VIEW_STATE.copy(selectedAgentName = ""), viewState)
    }

    @Test
    fun `update agent name when select it`() {
        // WHEN
        editSaleViewModel.onAgentSelected("New agent")

        // THEN
        verify(exactly = 1) { mockSetFormUseCase.updateAgent("New agent") }
    }

    @Test
    fun `open DatePicker at today date when click on empty entry date`() {
        // GIVEN
        getValueForTesting(editSaleViewModel.viewStateLiveData)

        // WHEN
        editSaleViewModel.onMarketEntryDateClicked()
        val showDatePickerEvent = getValueForTesting(editSaleViewModel.showDatePickerEventLiveData)

        // THEN
        assertEquals(
            ShowDatePickerEvent(
                type = EditSaleViewModel.DatePickerType.ENTRY_DATE,
                title = "Entry date",
                dateMillis = 1646092800000 // March, 1st 2022
            ),
            showDatePickerEvent
        )
    }

    @Test
    fun `open DatePicker at given date when click on non-empty entry date`() {
        // GIVEN
        FORM_FLOW.update { it.copy(marketEntryDate = "02/03/2022") }
        getValueForTesting(editSaleViewModel.viewStateLiveData)

        // WHEN
        editSaleViewModel.onMarketEntryDateClicked()
        val showDatePickerEvent = getValueForTesting(editSaleViewModel.showDatePickerEventLiveData)

        // THEN
        assertEquals(
            ShowDatePickerEvent(
                type = EditSaleViewModel.DatePickerType.ENTRY_DATE,
                title = "Entry date",
                dateMillis = 1646179200000 // March, 2nd 2022
            ),
            showDatePickerEvent
        )
    }

    @Test
    fun `open DatePicker at today date when click on empty sale date`() {
        // GIVEN
        FORM_FLOW.update { it.copy(saleDate = "") }
        getValueForTesting(editSaleViewModel.viewStateLiveData)

        // WHEN
        editSaleViewModel.onSaleDateClicked()
        val showDatePickerEvent = getValueForTesting(editSaleViewModel.showDatePickerEventLiveData)

        // THEN
        assertEquals(
            ShowDatePickerEvent(
                type = EditSaleViewModel.DatePickerType.SALE_DATE,
                title = "Sale date",
                dateMillis = 1646092800000 // March, 1st 2022
            ),
            showDatePickerEvent
        )
    }

    @Test
    fun `open DatePicker at given date when click on non-empty sale date`() {
        // GIVEN
        getValueForTesting(editSaleViewModel.viewStateLiveData)

        // WHEN
        editSaleViewModel.onSaleDateClicked()
        val showDatePickerEvent = getValueForTesting(editSaleViewModel.showDatePickerEventLiveData)

        // THEN
        assertEquals(
            ShowDatePickerEvent(
                type = EditSaleViewModel.DatePickerType.SALE_DATE,
                title = "Sale date",
                dateMillis = 1646179200000 // March, 2nd 2022
            ),
            showDatePickerEvent
        )
    }

    @Test
    fun `update entry date when select it`() {
        // GIVEN
        getValueForTesting(editSaleViewModel.viewStateLiveData)

        // WHEN
        editSaleViewModel.onDateSelected(1646092800000, EditSaleViewModel.DatePickerType.ENTRY_DATE)

        // THEN
        verify(exactly = 1) { mockSetFormUseCase.updateMarketEntryDate("01/03/2022") }
    }

    @Test
    fun `update sale date when select it`() {
        // GIVEN
        getValueForTesting(editSaleViewModel.viewStateLiveData)

        // WHEN
        editSaleViewModel.onDateSelected(1646179200000, EditSaleViewModel.DatePickerType.SALE_DATE)

        // THEN
        verify(exactly = 1) { mockSetFormUseCase.updateSaleDate("02/03/2022") }
    }

    @Test
    fun `update availability when switch it`() {
        // GIVEN
        getValueForTesting(editSaleViewModel.viewStateLiveData)

        // WHEN
        editSaleViewModel.onAvailabilitySwitched(true)

        // THEN
        verify(exactly = 1) { mockSetFormUseCase.updateAvailability(true) }
    }
}