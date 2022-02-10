package com.openclassrooms.realestatemanager.ui.add_edit.pages

data class SaleStatusViewState(
    val agentEntries: List<String>,
    val selectedAgentName: String,
    val marketEntryDate: String,
    val saleDate: String,
    val isAvailableForSale: Boolean,
)
