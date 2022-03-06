package com.openclassrooms.realestatemanager.ui.form.sale

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.UtilsRepository
import com.openclassrooms.realestatemanager.data.agent.AgentEntity
import com.openclassrooms.realestatemanager.data.form.FormEntity
import com.openclassrooms.realestatemanager.domain.GetAgentListUseCase
import com.openclassrooms.realestatemanager.domain.form.GetFormUseCase
import com.openclassrooms.realestatemanager.domain.form.SetFormUseCase
import com.openclassrooms.realestatemanager.ui.CoroutineProvider
import com.openclassrooms.realestatemanager.ui.SingleLiveEvent
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
    private val setFormUseCase: SetFormUseCase,
    getAgentListUseCase: GetAgentListUseCase,
    private val defaultClock: Clock,
    private val defaultZoneId: ZoneId,
    private val utilsRepository: UtilsRepository,
    coroutineProvider: CoroutineProvider,
    private val application: Application,
) : ViewModel() {

    val viewStateLiveData: LiveData<SaleViewState> = combine(
        getFormUseCase.getFormFlow(),
        getAgentListUseCase()
    ) { form: FormEntity, agentList: List<AgentEntity> ->
        currentForm = form

        val agentNameList = agentList.map { it.name }

        SaleViewState(
            agentEntries = agentNameList,
            selectedAgentName = agentNameList.firstOrNull { it == form.agentName } ?: "",
            marketEntryDate = form.marketEntryDate,
            marketEntryDateError = form.marketEntryDateError,
            saleDate = form.saleDate,
            saleDateError = form.saleDateError,
            isAvailableForSale = form.isAvailableForSale
        )
    }.asLiveData(coroutineProvider.getIoDispatcher())
    private val showDatePickerSingleLiveEvent = SingleLiveEvent<ShowDatePickerEvent>()
    val showDatePickerEventLiveData: LiveData<ShowDatePickerEvent> = showDatePickerSingleLiveEvent

    private lateinit var currentForm: FormEntity

    fun onAgentSelected(agentName: String) {
        setFormUseCase.updateAgent(agentName)
    }

    fun onMarketEntryDateClicked() {
        setDatePickerInfo(
            datePickerType = DatePickerType.ENTRY_DATE,
            titleId = R.string.picker_title_market_entry,
            dateString = currentForm.marketEntryDate
        )
    }

    fun onSaleDateClicked() {
        setDatePickerInfo(
            datePickerType = DatePickerType.SALE_DATE,
            titleId = R.string.picker_title_sale,
            dateString = currentForm.saleDate
        )
    }

    private fun setDatePickerInfo(
        datePickerType: DatePickerType,
        @StringRes titleId: Int,
        dateString: String
    ) {
        val pickerDate = if (dateString.isNotEmpty()) {
            utilsRepository.stringToDate(dateString)
        } else {
            LocalDate.now(defaultClock)
        }

        showDatePickerSingleLiveEvent.value = ShowDatePickerEvent(
            type = datePickerType,
            title = application.getString(titleId),
            dateMillis = pickerDate.atStartOfDay(defaultZoneId).toInstant().toEpochMilli()
        )
    }

    fun onDateSelected(dateMillis: Long, type: DatePickerType) {
        val selectedDateString = Instant.ofEpochMilli(dateMillis)
            .atZone(defaultZoneId)
            .toLocalDate()
            .format(UtilsRepository.DATE_FORMATTER)

        when (type) {
            DatePickerType.ENTRY_DATE -> setFormUseCase.updateMarketEntryDate(selectedDateString)
            DatePickerType.SALE_DATE -> setFormUseCase.updateSaleDate(selectedDateString)
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