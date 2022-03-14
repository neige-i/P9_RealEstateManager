package com.openclassrooms.realestatemanager.ui.list

import androidx.annotation.ColorRes

data class RealEstateViewState(
    val id: Long,
    val photoUrl: String,
    val type: String,
    val city: String,
    val price: String,
    @ColorRes val backgroundColor: Int,
    @ColorRes val priceTextColor: Int,
    @ColorRes val cityTextColor: Int,
)