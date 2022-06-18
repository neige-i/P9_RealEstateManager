package com.openclassrooms.realestatemanager.data.filter

import com.openclassrooms.realestatemanager.data.Localized
import com.openclassrooms.realestatemanager.data.PointOfInterest
import com.openclassrooms.realestatemanager.data.RealEstateType
import java.io.Serializable
import java.time.LocalDate

// Make it serializable to allow passing it as a Bundle argument
sealed class FilterValue : Serializable {

    sealed class MinMax : FilterValue() {
        abstract val min: Number
        abstract val max: Number
    }

    sealed class Choices : FilterValue() {
        abstract val selectedItems: List<Localized>
    }

    // ---------------------

    data class Price(
        override val min: Double,
        override val max: Double,
    ) : MinMax()

    data class Surface(
        override val min: Int,
        override val max: Int,
    ) : MinMax()

    data class PhotoCount(
        override val min: Int,
        override val max: Int,
    ) : MinMax()

    data class Date(
        val availableEstates: Boolean,
        val from: LocalDate?,
        val until: LocalDate?,
    ) : FilterValue()

    data class EstateType(
        override val selectedItems: List<RealEstateType>,
    ) : Choices()

    data class Poi(
        override val selectedItems: List<PointOfInterest>,
    ) : Choices()
}
