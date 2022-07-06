package com.openclassrooms.realestatemanager.ui.form.sale

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.UtilsRepository
import com.openclassrooms.realestatemanager.data.form.FormEntity
import com.openclassrooms.realestatemanager.data.real_estate.RealEstateRepository
import com.openclassrooms.realestatemanager.data.room.AgentEntity
import com.openclassrooms.realestatemanager.domain.form.GetFormUseCase
import com.openclassrooms.realestatemanager.domain.form.SetFormUseCase
import com.openclassrooms.realestatemanager.ui.util.CoroutineProvider
import com.openclassrooms.realestatemanager.ui.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class EditSaleViewModel @Inject constructor(
    getFormUseCase: GetFormUseCase,
    realEstateRepository: RealEstateRepository,
    private val setFormUseCase: SetFormUseCase,
    private val defaultClock: Clock,
    private val defaultZoneId: ZoneId,
    coroutineProvider: CoroutineProvider,
    private val application: Application,
) : ViewModel() {

    val viewStateLiveData: LiveData<SaleViewState> = combine(
        getFormUseCase.getFormFlow(),
        realEstateRepository.getAllAgents(),
    ) { form, agentList ->

        currentForm = form

        SaleViewState(
            allAgents = agentList,
            selectedAgentName = form.agent?.username.orEmpty(),
            marketEntryDate = form.marketEntryDate?.format(UtilsRepository.DATE_FORMATTER).orEmpty(),
            marketEntryDateError = form.marketEntryDateError,
            saleDate = form.saleDate?.format(UtilsRepository.DATE_FORMATTER).orEmpty(),
            saleDateError = form.saleDateError,
            isAvailableForSale = form.isAvailableForSale
        )
    }.asLiveData(coroutineProvider.getIoDispatcher())

    private val showDatePickerSingleLiveEvent = SingleLiveEvent<ShowDatePickerEvent>()
    val showDatePickerEventLiveData: LiveData<ShowDatePickerEvent> = showDatePickerSingleLiveEvent

    private lateinit var currentForm: FormEntity

    fun onAgentSelected(selectedAgent: AgentEntity) {
        setFormUseCase.updateAgent(selectedAgent)
    }

    fun onMarketEntryDateClicked() {
        setDatePickerInfo(
            datePickerType = DatePickerType.ENTRY_DATE,
            titleId = R.string.picker_title_market_entry,
            date = currentForm.marketEntryDate
        )
    }

    fun onSaleDateClicked() {
        setDatePickerInfo(
            datePickerType = DatePickerType.SALE_DATE,
            titleId = R.string.picker_title_sale,
            date = currentForm.saleDate
        )
    }

    private fun setDatePickerInfo(
        datePickerType: DatePickerType,
        @StringRes titleId: Int,
        date: LocalDate?,
    ) {
        showDatePickerSingleLiveEvent.value = ShowDatePickerEvent(
            type = datePickerType,
            title = application.getString(titleId),
            dateMillis = (date ?: LocalDate.now(defaultClock))
                .atStartOfDay(defaultZoneId)
                .toInstant()
                .toEpochMilli()
        )
    }

    fun onDateSelected(dateMillis: Long, type: DatePickerType) {
        val selectedDate = Instant.ofEpochMilli(dateMillis)
            .atZone(defaultZoneId)
            .toLocalDate()

        when (type) {
            DatePickerType.ENTRY_DATE -> setFormUseCase.updateMarketEntryDate(selectedDate)
            DatePickerType.SALE_DATE -> setFormUseCase.updateSaleDate(selectedDate)
        }
    }

    fun onAvailabilitySwitched(isAvailable: Boolean) {
        setFormUseCase.updateAvailability(isAvailable)
    }

    enum class DatePickerType {
        ENTRY_DATE,
        SALE_DATE,
    }
}