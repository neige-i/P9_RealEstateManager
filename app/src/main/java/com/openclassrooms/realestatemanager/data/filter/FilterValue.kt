package com.openclassrooms.realestatemanager.data.filter

import android.util.Range
import com.openclassrooms.realestatemanager.data.PointOfInterest
import com.openclassrooms.realestatemanager.data.RealEstateType
import java.time.LocalDate

sealed class FilterValue {

    data class IntMinMax(
        val value: Range<Int>,
    ) : FilterValue()

    data class DoubleMinMax(
        val value: Range<Double>,
    ) : FilterValue()

    data class DateMinMax(
        val availableEstates: Boolean,
        val min: LocalDate?,
        val max: LocalDate?,
    ) : FilterValue()

    data class TypeChoice(
        val value: List<RealEstateType>,
    ) : FilterValue()

    data class PoiChoice(
        val value: List<PointOfInterest>,
    ) : FilterValue()
}
