package com.openclassrooms.realestatemanager.ui.filter.date

import androidx.lifecycle.*
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.UtilsRepository
import com.openclassrooms.realestatemanager.data.filter.FilterRepository
import com.openclassrooms.realestatemanager.data.filter.FilterType
import com.openclassrooms.realestatemanager.data.filter.FilterValue
import com.openclassrooms.realestatemanager.domain.real_estate.GetAvailableValuesUseCase
import com.openclassrooms.realestatemanager.ui.filter.FilterViewModel
import com.openclassrooms.realestatemanager.ui.filter.date.DateFilterViewModel.DatePickerType.END
import com.openclassrooms.realestatemanager.ui.filter.date.DateFilterViewModel.DatePickerType.START
import com.openclassrooms.realestatemanager.ui.util.CoroutineProvider
import com.openclassrooms.realestatemanager.ui.util.SingleLiveEvent
import com.openclassrooms.realestatemanager.ui.util.update
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class DateFilterViewModel @Inject constructor(
    getAvailableValuesUseCase: GetAvailableValuesUseCase,
    coroutineProvider: CoroutineProvider,
    private val defaultZoneId: ZoneId,
    savedStateHandle: SavedStateHandle,
    filterRepository: FilterRepository,
) : FilterViewModel<FilterType.SaleStatus, FilterValue.Date>(savedStateHandle, filterRepository) {

    private val dateFilterMutableLiveData: MutableLiveData<FilterValue.Date?> = MutableLiveData(filterValue)

    // Use distinctUntilChanged() to avoid infinite loop due to RadioButton checking:
    // The RadioButton is checked while observing the view state but,
    // the view state is also updated when a RadioButton is checked
    val viewState = Transformations.map(dateFilterMutableLiveData.distinctUntilChanged()) { availableDates ->

        val startDateText = availableDates?.from?.format(UtilsRepository.DATE_FORMATTER).orEmpty()
        val endDateText = availableDates?.until?.format(UtilsRepository.DATE_FORMATTER).orEmpty()

        DateViewState(
            dialogTitle = R.string.filter_sale_dialog_title,
            selectedRadioBtn = when (availableDates?.availableEstates) {
                true -> R.id.filter_available_estates_radio_btn
                false -> R.id.filter_sold_estates_radio_btn
                null -> -1
            },
            isDateInputVisible = availableDates?.availableEstates != null,
            isStartDateEndIconVisible = startDateText.isNotEmpty(),
            startDateInputText = startDateText,
            isEndDateEndIconVisible = endDateText.isNotEmpty(),
            endDateInputText = endDateText,
        )
    }

    private val datePickerPingMutableLiveData = MutableLiveData<DatePickerType?>()
    private val showDatePickerSingleLiveEvent = SingleLiveEvent<ShowDatePickerEvent>()
    val viewEvent: LiveData<ShowDatePickerEvent> = showDatePickerSingleLiveEvent

    init {
        val dateLimitLiveData = combine(
            getAvailableValuesUseCase.getMinEntryDate(),
            getAvailableValuesUseCase.getMaxSaleDate(),
        ) { minEntryDate, maxSaleDate ->
            DateLimit(
                min = minEntryDate,
                max = maxSaleDate,
            )
        }.asLiveData(coroutineProvider.getIoDispatcher())

        showDatePickerSingleLiveEvent.addSource(dateFilterMutableLiveData) { dateFilter ->
            combineLiveEvent(dateFilter, dateLimitLiveData.value, datePickerPingMutableLiveData.value)
        }
        showDatePickerSingleLiveEvent.addSource(dateLimitLiveData) { dateLimit ->
            combineLiveEvent(dateFilterMutableLiveData.value, dateLimit, datePickerPingMutableLiveData.value)
        }
        showDatePickerSingleLiveEvent.addSource(datePickerPingMutableLiveData) { datePickerPing ->
            combineLiveEvent(dateFilterMutableLiveData.value, dateLimitLiveData.value, datePickerPing)
        }
    }

    private fun combineLiveEvent(dateFilter: FilterValue.Date?, dateLimit: DateLimit?, datePickerPing: DatePickerType?) {
        if (dateLimit == null || datePickerPing == null) {
            return
        }

        datePickerPingMutableLiveData.value = null // Reset ping

        val min = dateFilter?.from?.toMillis()
        val max = dateFilter?.until?.toMillis()
        val minLimit = dateLimit.min?.toMillis()
        val maxLimit = dateLimit.max?.toMillis()

        val onDateValidated = { millis: Long? ->
            updateDate(newDate = millis?.toDate(), datePickerType = datePickerPing)
        }

        showDatePickerSingleLiveEvent.value = when (datePickerPing) {
            START -> ShowDatePickerEvent(
                selectedDate = min,
                minConstraint = minLimit,
                maxConstraint = max ?: maxLimit,
                onValidated = onDateValidated,
            )
            END -> ShowDatePickerEvent(
                selectedDate = max,
                minConstraint = min ?: minLimit,
                maxConstraint = maxLimit,
                onValidated = onDateValidated,
            )
        }
    }

    fun onSaleStatusSelected(isAvailable: Boolean) {
        dateFilterMutableLiveData.update { availableDates ->
            availableDates?.copy(availableEstates = isAvailable)
                ?: FilterValue.Date(
                    availableEstates = isAvailable,
                    from = null,
                    until = null,
                )
        }
    }

    fun onDateInputClicked(datePickerType: DatePickerType) {
        datePickerPingMutableLiveData.value = datePickerType
    }

    fun onDateInputCleared(datePickerType: DatePickerType) {
        updateDate(newDate = null, datePickerType = datePickerType)
    }

    private fun updateDate(newDate: LocalDate?, datePickerType: DatePickerType) {
        dateFilterMutableLiveData.update { availableDates ->
            when (datePickerType) {
                START -> availableDates?.copy(from = newDate)
                END -> availableDates?.copy(until = newDate)
            }
        }
    }

    override fun getFilterToApply(): FilterValue.Date? = dateFilterMutableLiveData.value

    override fun onNeutralButtonClicked() {
        dateFilterMutableLiveData.value = null
    }

    private fun LocalDate.toMillis(): Long = atStartOfDay(defaultZoneId).toInstant().toEpochMilli()

    private fun Long.toDate() = Instant.ofEpochMilli(this).atZone(defaultZoneId).toLocalDate()

    data class DateLimit(
        val min: LocalDate?,
        val max: LocalDate?,
    )

    enum class DatePickerType {
        START,
        END,
    }
}