package com.openclassrooms.realestatemanager.data

import android.content.Context
import androidx.annotation.StringRes
import com.openclassrooms.realestatemanager.R

enum class RealEstateType(@StringRes val labelId: Int) {
    FLAT(R.string.real_estate_type_flat),
    DUPLEX(R.string.real_estate_type_duplex),
    HOUSE(R.string.real_estate_type_house),
    PENTHOUSE(R.string.real_estate_type_penthouse),
    ;

    companion object {
        fun fromLocaleString(localeString: String, context: Context): RealEstateType {
            return values().first { localeString == context.getString(it.labelId) }
        }
    }
}