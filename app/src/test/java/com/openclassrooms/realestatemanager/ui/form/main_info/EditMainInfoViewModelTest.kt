package com.openclassrooms.realestatemanager.ui.form.main_info

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.openclassrooms.realestatemanager.data.form.FormRepository
import com.openclassrooms.realestatemanager.domain.form.GetFormUseCase
import com.openclassrooms.realestatemanager.domain.form.SetFormUseCase
import com.openclassrooms.realestatemanager.ui.CoroutineProvider
import com.openclassrooms.realestatemanager.util.TestCoroutineRule
import com.openclassrooms.realestatemanager.util.TestLifecycle.getValueForTesting
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class EditMainInfoViewModelTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var mockGetFormUseCase: GetFormUseCase

    @MockK
    private lateinit var mockSetFormUseCase: SetFormUseCase

    @MockK
    private lateinit var mockCoroutineProvider: CoroutineProvider

    private lateinit var editMainInfoViewModel: EditMainInfoViewModel

    companion object {
        // region IN
        private val NOMINAL_FORM = FormRepository.DEFAULT_FORM.copy(
            type = "Flat",
            typeError = null,
            price = "123790.99",
            priceCursor = 4,
            area = "150",
            areaCursor = 3,
            totalRoomCount = 25,
            bathroomCount = 4,
            bedroomCount = 15,
        )
        // endregion IN

        // region OUT
        private val DEFAULT_VIEW_STATE = MainInfoViewState(
            selectedType = "Flat",
            typeError = null,
            price = "123790.99",
            priceSelection = 4,
            area = "150",
            areaSelection = 3,
            totalRoomCount = "25",
            bathroomCount = "4",
            bedroomCount = "15",
        )
        // endregion OUT
    }

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        every { mockGetFormUseCase.getFormFlow() } returns flowOf(NOMINAL_FORM)
        every { mockCoroutineProvider.getIoDispatcher() } returns testCoroutineRule.testCoroutineDispatcher
        justRun { mockSetFormUseCase.updateType(any()) }
        justRun { mockSetFormUseCase.updatePrice(any(), any()) }
        justRun { mockSetFormUseCase.updateArea(any(), any()) }
        justRun { mockSetFormUseCase.incTotalRoomCount() }
        justRun { mockSetFormUseCase.decTotalRoomCount() }
        justRun { mockSetFormUseCase.incBathroomCount() }
        justRun { mockSetFormUseCase.decBathroomCount() }
        justRun { mockSetFormUseCase.incBedroomCount() }
        justRun { mockSetFormUseCase.decBedroomCount() }

        editMainInfoViewModel = EditMainInfoViewModel(
            mockGetFormUseCase,
            mockSetFormUseCase,
            mockCoroutineProvider,
        )
    }

    @After
    fun tearDown() {
        verify(exactly = 1) { mockGetFormUseCase.getFormFlow() }
        verify(exactly = 1) { mockCoroutineProvider.getIoDispatcher() }
        confirmVerified(mockGetFormUseCase, mockSetFormUseCase, mockCoroutineProvider)
    }

    @Test
    fun `return default view state when get nominal form`() {
        // WHEN
        val viewState = getValueForTesting(editMainInfoViewModel.viewStateLiveData)

        // THEN
        assertEquals(DEFAULT_VIEW_STATE, viewState)
    }

    @Test
    fun `update type when change input`() {
        // WHEN
        editMainInfoViewModel.onTypeSelected("House")

        // THEN
        verify(exactly = 1) { mockSetFormUseCase.updateType("House") }
    }

    @Test
    fun `update price when change input with non-null content`() {
        // WHEN
        editMainInfoViewModel.onPriceChanged("45000", 0)

        // THEN
        verify(exactly = 1) { mockSetFormUseCase.updatePrice("45000", 0) }
    }

    @Test
    fun `update price when change input with null content`() {
        // WHEN
        editMainInfoViewModel.onPriceChanged(null, 3)

        // THEN
        verify(exactly = 1) { mockSetFormUseCase.updatePrice("", 3) }
    }

    @Test
    fun `update area when change input with non-null content`() {
        // WHEN
        editMainInfoViewModel.onAreaChanged("70", 2)

        // THEN
        verify(exactly = 1) { mockSetFormUseCase.updateArea("70", 2) }
    }

    @Test
    fun `update area when change input with null content`() {
        // WHEN
        editMainInfoViewModel.onAreaChanged(null, 2)

        // THEN
        verify(exactly = 1) { mockSetFormUseCase.updateArea("", 2) }
    }

    @Test
    fun `increment total room when add it`() {
        // WHEN
        editMainInfoViewModel.onTotalRoomAdded()

        // THEN
        verify(exactly = 1) { mockSetFormUseCase.incTotalRoomCount() }
    }

    @Test
    fun `decrement total room when remove it`() {
        // WHEN
        editMainInfoViewModel.onTotalRoomRemoved()

        // THEN
        verify(exactly = 1) { mockSetFormUseCase.decTotalRoomCount() }
    }

    @Test
    fun `increment bathroom when add it`() {
        // WHEN
        editMainInfoViewModel.onBathRoomAdded()

        // THEN
        verify(exactly = 1) { mockSetFormUseCase.incBathroomCount() }
    }

    @Test
    fun `decrement bathroom when remove it`() {
        // WHEN
        editMainInfoViewModel.onBathRoomRemoved()

        // THEN
        verify(exactly = 1) { mockSetFormUseCase.decBathroomCount() }
    }

    @Test
    fun `increment bedroom when add it`() {
        // WHEN
        editMainInfoViewModel.onBedRoomAdded()

        // THEN
        verify(exactly = 1) { mockSetFormUseCase.incBedroomCount() }
    }

    @Test
    fun `decrement bedroom when remove it`() {
        // WHEN
        editMainInfoViewModel.onBedRoomRemoved()

        // THEN
        verify(exactly = 1) { mockSetFormUseCase.decBedroomCount() }
    }
}