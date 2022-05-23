package com.openclassrooms.realestatemanager.data.filter

import androidx.annotation.StringRes
import com.openclassrooms.realestatemanager.R

enum class FilterType(@StringRes val labelId: Int) {
    TYPE(R.string.hint_type),
    PRICE(R.string.hint_price),
    SURFACE(R.string.hint_area),
    PHOTO_COUNT(R.string.filter_photo),
    POINT_OF_INTEREST(R.string.label_points_of_interest),
    SALE_STATUS(R.string.filter_sale_status),
}