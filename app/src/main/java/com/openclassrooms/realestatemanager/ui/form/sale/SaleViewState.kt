package com.openclassrooms.realestatemanager.ui.form.sale

import com.openclassrooms.realestatemanager.data.room.AgentEntity

data class SaleViewState(
    val allAgents: List<AgentEntity>,
    val selectedAgentName: String,
    val marketEntryDate: String,
    val marketEntryDateError: String?,
    val saleDate: String,
    val saleDateError: String?,
    val isAvailableForSale: Boolean,
)
