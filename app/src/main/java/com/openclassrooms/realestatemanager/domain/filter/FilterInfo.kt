package com.openclassrooms.realestatemanager.domain.filter

import android.util.Range
import com.openclassrooms.realestatemanager.data.PointOfInterest
import com.openclassrooms.realestatemanager.data.RealEstateType
import com.openclassrooms.realestatemanager.data.filter.FilterType
import java.time.LocalDate

sealed class FilterInfo {

    data class Ranges(
        val type: FilterType,
        val outerRange: Range<Float>,
        val innerRange: Range<Float>,
        val step: Float,
    ) : FilterInfo()

    data class Dates(
        val minDateLimit: LocalDate?,
        val maxDateLimit: LocalDate?,
        val min: LocalDate?,
        val max: LocalDate?,
        val availableEstates: Boolean?,
    ) : FilterInfo()

    sealed class MultiChoice<T>(
        open val type: FilterType,
        open val selection: List<T>,
    ) : FilterInfo() {

        data class Type(
            override val type: FilterType,
            override val selection: List<RealEstateType>,
        ) : MultiChoice<RealEstateType>(type, selection)

        data class Poi(
            override val type: FilterType,
            override val selection: List<PointOfInterest>,
        ) : MultiChoice<PointOfInterest>(type, selection)
    }
}
