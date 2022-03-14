package com.openclassrooms.realestatemanager.ui.form.address

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.form.FormRepository
import com.openclassrooms.realestatemanager.domain.form.GetFormUseCase
import com.openclassrooms.realestatemanager.domain.form.SetFormUseCase
import com.openclassrooms.realestatemanager.ui.util.CoroutineProvider
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
class EditAddressViewModelTest {

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

    private lateinit var editAddressViewModel: EditAddressViewModel

    companion object {
        // region IN
        private val NOMINAL_FORM = FormRepository.DEFAULT_FORM.copy(
            streetName = "740 Park Avenue",
            streetNameError = null,
            streetNameCursor = 5,
            additionalAddressInfo = "Apt 6/7A",
            additionalAddressInfoCursor = 1,
            city = "New York",
            cityError = "ERR",
            cityCursor = 6,
            state = "NY",
            stateError = null,
            stateCursor = 2,
            zipcode = "10021",
            zipcodeError = null,
            zipcodeCursor = 1,
            country = "United",
            countryError = "Wrong",
            countryCursor = 9,
            pointsOfInterests = listOf(R.string.label_poi_bar, R.string.label_poi_park),
        )
        // endregion IN

        // region OUT
        private val DEFAULT_VIEW_STATE = AddressViewState(
            streetNumber = "740 Park Avenue",
            streetNumberError = null,
            streetNumberSelection = 5,
            additionalInfo = "Apt 6/7A",
            additionalInfoSelection = 1,
            city = "New York",
            cityError = "ERR",
            citySelection = 6,
            state = "NY",
            stateError = null,
            stateSelection = 2,
            zipcode = "10021",
            zipcodeError = null,
            zipcodeSelection = 1,
            country = "United",
            countryError = "Wrong",
            countrySelection = 9,
            pointOfInterestList = listOf(
                AddressViewState.ChipViewState(R.string.label_poi_bar, true), // Checked
                AddressViewState.ChipViewState(R.string.label_poi_cafe, false),
                AddressViewState.ChipViewState(R.string.label_poi_restaurant, false),
                AddressViewState.ChipViewState(R.string.label_poi_hospital, false),
                AddressViewState.ChipViewState(R.string.label_poi_movie_theater, false),
                AddressViewState.ChipViewState(R.string.label_poi_park, true), // Checked
                AddressViewState.ChipViewState(R.string.label_poi_stadium, false),
                AddressViewState.ChipViewState(R.string.label_poi_shopping_mall, false),
                AddressViewState.ChipViewState(R.string.label_poi_school, false),
                AddressViewState.ChipViewState(R.string.label_poi_university, false),
                AddressViewState.ChipViewState(R.string.label_poi_subway_station, false),
                AddressViewState.ChipViewState(R.string.label_poi_train_station, false),
            )
        )
        // endregion OUT
    }

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        every { mockGetFormUseCase.getFormFlow() } returns flowOf(NOMINAL_FORM)
        every { mockCoroutineProvider.getIoDispatcher() } returns testCoroutineRule.testCoroutineDispatcher
        justRun { mockSetFormUseCase.updateStreetName(any(), any()) }
        justRun { mockSetFormUseCase.updateAdditionalAddressInfo(any(), any()) }
        justRun { mockSetFormUseCase.updateCity(any(), any()) }
        justRun { mockSetFormUseCase.updateState(any(), any()) }
        justRun { mockSetFormUseCase.updateZipcode(any(), any()) }
        justRun { mockSetFormUseCase.updateCountry(any(), any()) }
        justRun { mockSetFormUseCase.updatePoi(any(), any()) }

        editAddressViewModel = EditAddressViewModel(
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
        val viewState = getValueForTesting(editAddressViewModel.viewStateLiveData)

        // THEN
        assertEquals(DEFAULT_VIEW_STATE, viewState)
    }

    @Test
    fun `update street name when change input with non-null content`() {
        // WHEN
        editAddressViewModel.onStreetNameChanged("Main street", 4)

        // THEN
        verify(exactly = 1) { mockSetFormUseCase.updateStreetName("Main street", 4) }
    }

    @Test
    fun `update street name when change input with null content`() {
        // WHEN
        editAddressViewModel.onStreetNameChanged(null, 4)

        // THEN
        verify(exactly = 1) { mockSetFormUseCase.updateStreetName("", 4) }
    }

    @Test
    fun `update additional address when change input with non-null content`() {
        // WHEN
        editAddressViewModel.onAdditionalAddressInfoChanged("2nd floor", 3)

        // THEN
        verify(exactly = 1) { mockSetFormUseCase.updateAdditionalAddressInfo("2nd floor", 3) }
    }

    @Test
    fun `update additional address when change input with null content`() {
        // WHEN
        editAddressViewModel.onAdditionalAddressInfoChanged(null, 2)

        // THEN
        verify(exactly = 1) { mockSetFormUseCase.updateAdditionalAddressInfo("", 2) }
    }

    @Test
    fun `update city when change input with non-null content`() {
        // WHEN
        editAddressViewModel.onCityChanged("New York", 1)

        // THEN
        verify(exactly = 1) { mockSetFormUseCase.updateCity("New York", 1) }
    }

    @Test
    fun `update city when change input with null content`() {
        // WHEN
        editAddressViewModel.onCityChanged(null, 1)

        // THEN
        verify(exactly = 1) { mockSetFormUseCase.updateCity("", 1) }
    }

    @Test
    fun `update state when change input with non-null content`() {
        // WHEN
        editAddressViewModel.onStateNameChanged("NY", 0)

        // THEN
        verify(exactly = 1) { mockSetFormUseCase.updateState("NY", 0) }
    }

    @Test
    fun `update state when change input with null content`() {
        // WHEN
        editAddressViewModel.onStateNameChanged(null, 0)

        // THEN
        verify(exactly = 1) { mockSetFormUseCase.updateState("", 0) }
    }

    @Test
    fun `update zipcode when change input with non-null content`() {
        // WHEN
        editAddressViewModel.onZipcodeChanged("50111", 5)

        // THEN
        verify(exactly = 1) { mockSetFormUseCase.updateZipcode("50111", 5) }
    }

    @Test
    fun `update zipcode when change input with null content`() {
        // WHEN
        editAddressViewModel.onZipcodeChanged(null, 5)

        // THEN
        verify(exactly = 1) { mockSetFormUseCase.updateZipcode("", 5) }
    }

    @Test
    fun `update country when change input with non-null content`() {
        // WHEN
        editAddressViewModel.onCountryChanged("United States", 0)

        // THEN
        verify(exactly = 1) { mockSetFormUseCase.updateCountry("United States", 0) }
    }

    @Test
    fun `update country when change input with null content`() {
        // WHEN
        editAddressViewModel.onCountryChanged(null, 0)

        // THEN
        verify(exactly = 1) { mockSetFormUseCase.updateCountry("", 0) }
    }

    @Test
    fun `update POI when change item is (un)checked`() {
        // WHEN
        editAddressViewModel.onPoiChecked(123, true)

        // THEN
        verify(exactly = 1) { mockSetFormUseCase.updatePoi(123, true) }
    }
}