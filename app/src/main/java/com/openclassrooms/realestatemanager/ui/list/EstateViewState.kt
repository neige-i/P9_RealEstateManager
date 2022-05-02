package com.openclassrooms.realestatemanager.ui.list

import androidx.annotation.ColorRes

data class EstateViewState(
    val id: Long,
    val photoUrl: String,
    val type: String,
    val city: String,
    val price: String,
    val style: Style,
) {

    data class Style(
        @ColorRes val backgroundColor: Int,
        @ColorRes val priceTextColor: Int,
        @ColorRes val cityTextColor: Int,
    )
}