package com.openclassrooms.realestatemanager.data

import androidx.annotation.StringRes
import com.openclassrooms.realestatemanager.R

enum class RealEstateType(@StringRes val labelId: Int) {
    FLAT(R.string.real_estate_type_flat),
    DUPLEX(R.string.real_estate_type_duplex),
    HOUSE(R.string.real_estate_type_house),
    PENTHOUSE(R.string.real_estate_type_penthouse),
}