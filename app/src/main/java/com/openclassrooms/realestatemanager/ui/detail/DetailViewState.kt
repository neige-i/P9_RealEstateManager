package com.openclassrooms.realestatemanager.ui.detail

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.openclassrooms.realestatemanager.ui.util.LocalText

sealed class DetailViewState(
    val isNoSelectionLabelVisible: Boolean,
) {

    data class Empty(
        val noSelectionLabelText: LocalText,
    ) : DetailViewState(isNoSelectionLabelVisible = true)

    data class Info(
        @StringRes val type: Int,
        val price: LocalText,
        val areTypeAndPriceVisible: Boolean,
        @StringRes val saleText: Int,
        @ColorRes val saleBackgroundColor: Int,
        val photoList: List<Photo>,
        val description: LocalText,
        val surface: LocalText,
        val roomCount: String,
        val bathroomCount: String,
        val bedroomCount: String,
        val address: String,
        val poiList: List<Int>,
        val mapUrl: String,
        val marketDates: LocalText,
        val agentName: LocalText,
    ) : DetailViewState(isNoSelectionLabelVisible = false) {

        data class Photo(
            val url: String,
            val description: String,
            val onClicked: () -> Unit,
        )
    }
}
