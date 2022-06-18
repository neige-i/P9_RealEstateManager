package com.openclassrooms.realestatemanager.data.filter

import java.io.Serializable

// Make it serializable to allow passing it as a Bundle argument
sealed class FilterType : Serializable {

    sealed class Slider : FilterType()
    sealed class CheckList : FilterType()

    object EstateType : CheckList()
    object Price : Slider()
    object Surface : Slider()
    object PhotoCount : Slider()
    object PointOfInterest : CheckList()
    object SaleStatus : FilterType()
}