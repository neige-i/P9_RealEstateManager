package com.openclassrooms.realestatemanager.domain.filter

import android.util.Range
import com.openclassrooms.realestatemanager.data.PointOfInterest
import com.openclassrooms.realestatemanager.data.RealEstateType
import com.openclassrooms.realestatemanager.data.filter.FilterRepository
import com.openclassrooms.realestatemanager.data.filter.FilterType
import com.openclassrooms.realestatemanager.data.filter.FilterValue
import javax.inject.Inject

class SetFilterUseCase @Inject constructor(
    private val filterRepository: FilterRepository,
) {

    fun applyFilter(sliderType: FilterType.Slider, selection: Range<Float>?, bounds: Range<Float>) {
        val minMaxValue = if (selection != null && selection != bounds) {
            when (sliderType) {
                is FilterType.PhotoCount -> FilterValue.PhotoCount(min = selection.lower.toInt(), max = selection.upper.toInt())
                is FilterType.Price -> FilterValue.Price(min = selection.lower.toDouble(), max = selection.upper.toDouble())
                is FilterType.Surface -> FilterValue.Surface(min = selection.lower.toInt(), max = selection.upper.toInt())
            }
        } else {
            null // Reset filter
        }

        filterRepository.applyFilter(filterToSet = sliderType, filterToApply = minMaxValue)
    }

    fun applyFilter(checkListType: FilterType.CheckList, selection: MutableList<Int>?) {
        val choicesValue = if (selection.isNullOrEmpty()) {
            null // Reset filter
        } else {
            when (checkListType) {
                FilterType.EstateType -> FilterValue.EstateType(selection.map { RealEstateType.values().first { estateType -> estateType.labelId == it } })
                FilterType.PointOfInterest -> FilterValue.Poi(selection.map { PointOfInterest.values().first { poi -> poi.labelId == it } })
            }
        }

        filterRepository.applyFilter(filterToSet = checkListType, filterToApply = choicesValue)
    }

    fun applyFilter(availableDates: FilterValue.AvailableDates?) {
        filterRepository.applyFilter(filterToSet = FilterType.SaleStatus, filterToApply = availableDates)
    }

    fun reset(filterType: FilterType) {
        filterRepository.applyFilter(filterType, null)
    }
}