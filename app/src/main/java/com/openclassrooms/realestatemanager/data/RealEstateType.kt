package com.openclassrooms.realestatemanager.data

import androidx.annotation.StringRes
import com.openclassrooms.realestatemanager.R

enum class RealEstateType(@StringRes val labelId: Int) : Localized {
    FLAT(R.string.real_estate_type_flat),
    DUPLEX(R.string.real_estate_type_duplex),
    HOUSE(R.string.real_estate_type_house),
    PENTHOUSE(R.string.real_estate_type_penthouse),
    ;

    override val stringId: Int = labelId

    override fun fromStringId(@StringRes stringId: Int): Localized = values().first { it.labelId == this.stringId }
}