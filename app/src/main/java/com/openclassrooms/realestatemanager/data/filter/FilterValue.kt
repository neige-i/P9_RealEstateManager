package com.openclassrooms.realestatemanager.data.filter

import com.openclassrooms.realestatemanager.data.PointOfInterest
import com.openclassrooms.realestatemanager.data.RealEstateType
import java.io.Serializable
import java.time.LocalDate

// Make it serializable to allow passing it as a Bundle argument
sealed class FilterValue : Serializable {

    sealed class MinMax<T: Number> : FilterValue() {
        abstract val min: T
        abstract val max: T
    }
    sealed class Choices : FilterValue()

    // ---------------------

    data class Price(
        override val min: Double,
        override val max: Double,
    ) : MinMax<Double>()

    data class Surface(
        override val min: Int,
        override val max: Int,
    ) : MinMax<Int>()

    data class PhotoCount(
        override val min: Int,
        override val max: Int,
    ) : MinMax<Int>()

    data class AvailableDates(
        val availableEstates: Boolean,
        val from: LocalDate?,
        val until: LocalDate?,
    ) : FilterValue()

    data class EstateType(
        val selectedEstateTypes: List<RealEstateType>,
    ) : Choices()

    data class Poi(
        val selectedPois: List<PointOfInterest>,
    ) : Choices()
}
