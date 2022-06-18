package com.openclassrooms.realestatemanager.domain.filter

import android.util.Range
import com.openclassrooms.realestatemanager.data.Localized
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
        if (selection != null && selection != bounds) {
            filterRepository.apply(
                filterToSet = sliderType,
                filterToApply = when (sliderType) {
                    is FilterType.PhotoCount -> FilterValue.PhotoCount(min = selection.lower.toInt(), max = selection.upper.toInt())
                    is FilterType.Price -> FilterValue.Price(min = selection.lower.toDouble(), max = selection.upper.toDouble())
                    is FilterType.Surface -> FilterValue.Surface(min = selection.lower.toInt(), max = selection.upper.toInt())
                }
            )
        } else {
            filterRepository.clear(sliderType)
        }
    }

    fun applyFilter(checkListType: FilterType.CheckList, selection: List<Localized>?) {
        if (!selection.isNullOrEmpty()) {
            filterRepository.apply(
                filterToSet = checkListType,
                filterToApply = when (checkListType) {
                    FilterType.EstateType -> FilterValue.EstateType(selection.map { it as RealEstateType })
                    FilterType.PointOfInterest -> FilterValue.Poi(selection.map { it as PointOfInterest })
                }
            )
        } else {
            filterRepository.clear(checkListType)
        }
    }

    fun applyFilter(date: FilterValue.Date?) {
        if (date != null) {
            filterRepository.apply(filterToSet = FilterType.SaleStatus, filterToApply = date)
        } else {
            filterRepository.clear(FilterType.SaleStatus)
        }
    }
}