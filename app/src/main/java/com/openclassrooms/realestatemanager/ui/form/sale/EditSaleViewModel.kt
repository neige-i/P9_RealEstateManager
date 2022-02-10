package com.openclassrooms.realestatemanager.ui.form.sale

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.data.agent.AgentEntity
import com.openclassrooms.realestatemanager.data.form.FormEntity
import com.openclassrooms.realestatemanager.domain.form.SetFormUseCase
import com.openclassrooms.realestatemanager.domain.GetAgentListUseCase
import com.openclassrooms.realestatemanager.domain.form.GetFormUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditSaleViewModel @Inject constructor(
    getFormUseCase: GetFormUseCase,
    private val setFormUseCase: SetFormUseCase,
    getAgentListUseCase: GetAgentListUseCase,
) : ViewModel() {

    private val viewStateMediatorLiveData = MediatorLiveData<SaleViewState>()
    val viewStateLiveData: LiveData<SaleViewState> = viewStateMediatorLiveData

    init {
        val tempRealEstateLiveData = getFormUseCase.getUpdates()
        val agentListLiveData = getAgentListUseCase()

        viewStateMediatorLiveData.addSource(tempRealEstateLiveData) {
            combine(it, agentListLiveData.value)
        }
        viewStateMediatorLiveData.addSource(agentListLiveData) {
            combine(tempRealEstateLiveData.value, it)
        }
    }

    private fun combine(form: FormEntity?, agentList: List<AgentEntity>?) {
        if (form == null || agentList == null) {
            return
        }

        val agentNameList = agentList.map { it.name }
        val selectedAgentName = agentNameList.firstOrNull { it == form.agentName } ?: ""

        viewStateMediatorLiveData.value = SaleViewState(
            agentEntries = agentNameList,
            selectedAgentName = selectedAgentName,
            marketEntryDate = form.marketEntryDate,
            saleDate = form.saleDate,
            saleDateError = form.saleDateError,
            isAvailableForSale = form.isAvailableForSale
        )
    }

    fun onAgentSelected(agentName: String) {
        setFormUseCase.updateAgent(agentName)
    }

    fun onAvailabilitySwitched(isAvailable: Boolean) {
        setFormUseCase.updateAvailability(isAvailable)

        if (!isAvailable) {
            setFormUseCase.updateSaleDate("")
        }
    }

    fun onMarketEntryDateChanged(marketEntryDate: String?) {
        setFormUseCase.updateMarketEntryDate(marketEntryDate ?: "")
    }

    fun onSaleDateChanged(saleDate: String?) {
        setFormUseCase.updateSaleDate(saleDate ?: "")
    }
}