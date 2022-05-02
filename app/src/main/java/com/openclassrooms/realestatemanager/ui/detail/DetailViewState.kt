package com.openclassrooms.realestatemanager.ui.detail

import androidx.annotation.ColorRes

sealed class DetailViewState(
    val isNoSelectionLabelVisible: Boolean,
) {

    data class Empty(
        val noSelectionLabelText: String,
    ) : DetailViewState(isNoSelectionLabelVisible = true)

    data class WithInfo(
        val type: String,
        val price: String,
        val areTypeAndPriceVisible: Boolean,
        val saleText: String,
        @ColorRes val saleBackgroundColor: Int,
        val photoList: List<Photo>,
        val description: String,
        val surface: String,
        val roomCount: String,
        val bathroomCount: String,
        val bedroomCount: String,
        val address: String,
        val poiList: List<Int>,
        val mapUrl: String,
        val market_dates: String,
        val agentName: String,
    ) : DetailViewState(isNoSelectionLabelVisible = false) {

        data class Photo(
            val url: String,
            val description: String,
        )
    }
}
