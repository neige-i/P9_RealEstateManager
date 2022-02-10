package com.openclassrooms.realestatemanager.ui.add_edit.pages

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.data.agent.AgentEntity
import com.openclassrooms.realestatemanager.data.form.FormEntity
import com.openclassrooms.realestatemanager.domain.EditFormUseCase
import com.openclassrooms.realestatemanager.domain.GetAgentListUseCase
import com.openclassrooms.realestatemanager.domain.GetFormInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SaleStatusViewModel @Inject constructor(
    getFormInfoUseCase: GetFormInfoUseCase,
    private val editFormUseCase: EditFormUseCase,
    getAgentListUseCase: GetAgentListUseCase,
) : ViewModel() {

    private val viewStateMediatorLiveData = MediatorLiveData<SaleStatusViewState>()
    val viewState: LiveData<SaleStatusViewState> = viewStateMediatorLiveData

    init {
        val tempRealEstateLiveData = getFormInfoUseCase()
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

        viewStateMediatorLiveData.value = SaleStatusViewState(
            agentEntries = agentNameList,
            selectedAgentName = selectedAgentName,
            marketEntryDate = form.marketEntryDate,
            saleDate = form.saleDate,
            isAvailableForSale = form.isAvailableForSale
        )
    }

    fun onAgentSelected(agentName: String) {
        editFormUseCase.updateAgent(agentName)
    }

    fun onAvailabilitySwitched(isAvailable: Boolean) {
        editFormUseCase.updateAvailability(isAvailable)

        if (!isAvailable) {
            editFormUseCase.updateSaleDate("")
        }
    }

    fun onMarketEntryDateChanged(marketEntryDate: String?) {
        editFormUseCase.updateMarketEntryDate(marketEntryDate ?: "")
    }

    fun onSaleDateChanged(saleDate: String?) {
        editFormUseCase.updateSaleDate(saleDate ?: "")
    }
}