package com.openclassrooms.realestatemanager.ui.form.sale

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.agent.AgentEntity
import com.openclassrooms.realestatemanager.data.form.FormEntity
import com.openclassrooms.realestatemanager.data.form.FormRepository
import com.openclassrooms.realestatemanager.domain.GetAgentListUseCase
import com.openclassrooms.realestatemanager.domain.form.GetFormUseCase
import com.openclassrooms.realestatemanager.domain.form.SetFormUseCase
import com.openclassrooms.realestatemanager.ui.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val application: Application,
) : ViewModel() {

    companion object {
        private const val MARKET_ENTRY_DATE = 0
        private const val SALE_DATE = 1
    }

    private val viewStateMediatorLiveData = MediatorLiveData<SaleViewState>()
    val viewStateLiveData: LiveData<SaleViewState> = viewStateMediatorLiveData
    private val showDatePickerSingleLiveEvent = SingleLiveEvent<ShowDatePickerEvent>()
    val showDatePickerEventLiveData: LiveData<ShowDatePickerEvent> = showDatePickerSingleLiveEvent

    private var currentForm: FormEntity? = null

    private var whichDatePicker = -1

    init {
        val formLiveData = getFormUseCase.getUpdates()
        val agentListLiveData = getAgentListUseCase()

        viewStateMediatorLiveData.addSource(formLiveData) {
            combine(it, agentListLiveData.value)
        }
        viewStateMediatorLiveData.addSource(agentListLiveData) {
            combine(formLiveData.value, it)
        }
    }

    private fun combine(form: FormEntity?, agentList: List<AgentEntity>?) {
        if (form == null || agentList == null) {
            return
        }

        currentForm = form

        val agentNameList = agentList.map { it.name }

        viewStateMediatorLiveData.value = SaleViewState(
            agentEntries = agentNameList,
            selectedAgentName = agentNameList.firstOrNull { it == form.agentName } ?: "",
            marketEntryDate = form.marketEntryDate,
            marketEntryDateError = form.marketEntryDateError,
            saleDate = form.saleDate,
            saleDateError = form.saleDateError,
            isAvailableForSale = form.isAvailableForSale
        )
    }

    fun onAgentSelected(agentName: String) {
        setFormUseCase.updateAgent(agentName)
    }

    fun onMarketEntryDateClicked() {
        whichDatePicker = MARKET_ENTRY_DATE
        setDatePickerInfo(currentForm?.marketEntryDate, R.string.picker_title_market_entry)
    }

    fun onSaleDateClicked() {
        whichDatePicker = SALE_DATE
        setDatePickerInfo(currentForm?.saleDate, R.string.picker_title_sale)
    }

    private fun setDatePickerInfo(dateString: String?, @StringRes titleId: Int) {
        val pickerDate = if (dateString?.isNotEmpty() == true) {
            LocalDate.parse(dateString, FormRepository.DATE_FORMATTER)
        } else {
            LocalDate.now(defaultClock)
        }

        showDatePickerSingleLiveEvent.value = ShowDatePickerEvent(
            title = application.getString(titleId),
            dateMillis = pickerDate.atStartOfDay(defaultZoneId).toInstant().toEpochMilli()
        )
    }

    fun onDateSelected(dateMillis: Long) {
        val selectedDateString = Instant.ofEpochMilli(dateMillis)
            .atZone(defaultZoneId)
            .toLocalDate()
            .format(FormRepository.DATE_FORMATTER)

        when (whichDatePicker) {
            MARKET_ENTRY_DATE -> setFormUseCase.updateMarketEntryDate(selectedDateString)
            SALE_DATE -> setFormUseCase.updateSaleDate(selectedDateString)
        }

        whichDatePicker = -1
    }

    fun onAvailabilitySwitched(isAvailable: Boolean) {
        setFormUseCase.updateAvailability(isAvailable)

        if (!isAvailable) {
            setFormUseCase.updateSaleDate("")
        }
    }
}