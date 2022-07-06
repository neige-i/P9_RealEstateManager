package com.openclassrooms.realestatemanager.ui.list

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.openclassrooms.realestatemanager.ui.util.LocalText

data class EstateViewState(
    val id: Long,
    val photoUrl: String,
    @StringRes val type: Int,
    val city: String,
    val price: LocalText,
    val style: Style,
    val onClicked: () -> Unit,
) {

    data class Style(
        @ColorRes val backgroundColor: Int,
        @ColorRes val priceTextColor: Int,
        @ColorRes val cityTextColor: Int,
    )
}