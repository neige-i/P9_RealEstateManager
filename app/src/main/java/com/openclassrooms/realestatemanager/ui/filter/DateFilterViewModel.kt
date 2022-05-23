package com.openclassrooms.realestatemanager.ui.filter

import androidx.annotation.IdRes
import androidx.lifecycle.*
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.UtilsRepository
import com.openclassrooms.realestatemanager.domain.filter.FilterInfo
import com.openclassrooms.realestatemanager.domain.filter.GetFilterUseCase
import com.openclassrooms.realestatemanager.domain.filter.SetFilterUseCase
import com.openclassrooms.realestatemanager.ui.filter.DateFilterViewModel.DatePickerType.END
import com.openclassrooms.realestatemanager.ui.filter.DateFilterViewModel.DatePickerType.START
import com.openclassrooms.realestatemanager.ui.util.CoroutineProvider
import com.openclassrooms.realestatemanager.ui.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class DateFilterViewModel @Inject constructor(
    getFilterUseCase: GetFilterUseCase,
    private val setFilterUseCase: SetFilterUseCase,
    private val coroutineProvider: CoroutineProvider,
    private val defaultZoneId: ZoneId,
) : ViewModel() {

    private val openDatePickerMutableLiveData = MutableLiveData<DatePickerType?>()
    private val datesFilterInfoMutableLiveData = MutableLiveData<FilterInfo.Dates>().apply {
        viewModelScope.launch {
            val datesFilterInfo = getFilterUseCase().first() as FilterInfo.Dates

            withContext(coroutineProvider.getMainDispatcher()) {
                value = datesFilterInfo
            }
        }
    }

    // Use distinctUntilChanged() to avoid infinite loop due to RadioButton checking:
    // The RadioButton is checked while observing the view state but,
    // the view state is also updated when a RadioButton is checked
    val viewState =
        Transformations.map(datesFilterInfoMutableLiveData.distinctUntilChanged()) { datesFilter ->
            val startDateText = datesFilter.min?.format(UtilsRepository.DATE_FORMATTER).orEmpty()
            val endDateText = datesFilter.max?.format(UtilsRepository.DATE_FORMATTER).orEmpty()

            DateFilterViewState(
                selectedRadioBtn = when (datesFilter.availableEstates) {
                    true -> R.id.filter_available_estates_radio_btn
                    false -> R.id.filter_sold_estates_radio_btn
                    null -> -1
                },
                isDateInputVisible = datesFilter.availableEstates != null,
                isStartDateEndIconVisible = startDateText.isNotEmpty(),
                startDateInputText = startDateText,
                isEndDateEndIconVisible = endDateText.isNotEmpty(),
                endDateInputText = endDateText,
            )
        }

    private val showDatePickerSingleLiveEvent = SingleLiveEvent<ShowDatePickerEvent>()
    val showDatePickerEvent: LiveData<ShowDatePickerEvent> = showDatePickerSingleLiveEvent

    init {
        showDatePickerSingleLiveEvent.addSource(datesFilterInfoMutableLiveData) { datesFilterInfo ->
            combineLiveEvent(datesFilterInfo, openDatePickerMutableLiveData.value)
        }
        showDatePickerSingleLiveEvent.addSource(openDatePickerMutableLiveData) { openDatePicker ->
            combineLiveEvent(datesFilterInfoMutableLiveData.value, openDatePicker)
        }
    }

    private fun combineLiveEvent(
        datesFilterInfo: FilterInfo.Dates?,
        openDatePicker: DatePickerType?,
    ) {
        if (datesFilterInfo == null || openDatePicker == null) {
            return
        }

        openDatePickerMutableLiveData.value = null // Reset ping

        val min = datesFilterInfo.min?.toMillis()
        val max = datesFilterInfo.max?.toMillis()
        val minLimit = datesFilterInfo.minDateLimit?.toMillis()
        val maxLimit = datesFilterInfo.maxDateLimit?.toMillis()

        val onDateValidated = { millis: Long? -> updateDate(millis?.toDate(), openDatePicker) }

        showDatePickerSingleLiveEvent.value = when (openDatePicker) {
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

    fun onSaleStatusSelected(@IdRes checkedId: Int) {
        datesFilterInfoMutableLiveData.update {
            it.copy(
                availableEstates = when (checkedId) {
                    R.id.filter_available_estates_radio_btn -> true
                    R.id.filter_sold_estates_radio_btn -> false
                    else -> null
                }
            )
        }
    }

    fun onDateInputClicked(datePickerType: DatePickerType) {
        openDatePickerMutableLiveData.value = datePickerType
    }

    fun onDateInputCleared(datePickerType: DatePickerType) {
        updateDate(newDate = null, datePickerType = datePickerType)
    }

    private fun updateDate(newDate: LocalDate?, datePickerType: DatePickerType) {
        datesFilterInfoMutableLiveData.update {
            when (datePickerType) {
                START -> it.copy(min = newDate)
                END -> it.copy(max = newDate)
            }
        }
    }

    fun onPositiveButtonClicked() {
        val currentDatesFilter = datesFilterInfoMutableLiveData.value ?: return

        viewModelScope.launch(coroutineProvider.getIoDispatcher()) {
            setFilterUseCase.applyDates(currentDatesFilter)
        }
    }

    fun onNeutralButtonClicked() {
        datesFilterInfoMutableLiveData.update {
            it.copy(
                min = null,
                max = null,
                availableEstates = null,
            )
        }
    }

    private fun LocalDate.toMillis(): Long = atStartOfDay(defaultZoneId).toInstant().toEpochMilli()

    private fun Long.toDate() = Instant.ofEpochMilli(this).atZone(defaultZoneId).toLocalDate()

    private inline fun <T> MutableLiveData<T>.update(function: (T) -> T) {
        value?.let { value = function(it) }
    }

    enum class DatePickerType {
        START,
        END,
    }
}