package com.openclassrooms.realestatemanager.ui.form.sale

data class ShowDatePickerEvent(
    val type: EditSaleViewModel.DatePickerType,
    val title: String,
    val dateMillis: Long,
)
