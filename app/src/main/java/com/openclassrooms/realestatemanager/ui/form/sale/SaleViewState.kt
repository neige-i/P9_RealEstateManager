package com.openclassrooms.realestatemanager.ui.form.sale

data class SaleViewState(
    val agentEntries: List<String>,
    val selectedAgentName: String,
    val marketEntryDate: String,
    val saleDate: String,
    val isAvailableForSale: Boolean,
)
