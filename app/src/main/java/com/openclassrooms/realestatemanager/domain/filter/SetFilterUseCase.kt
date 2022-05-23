package com.openclassrooms.realestatemanager.domain.filter

import android.util.Range
import com.openclassrooms.realestatemanager.data.PointOfInterest
import com.openclassrooms.realestatemanager.data.RealEstateType
import com.openclassrooms.realestatemanager.data.filter.FilterType
import com.openclassrooms.realestatemanager.data.filter.FilterRepository
import com.openclassrooms.realestatemanager.data.filter.FilterType.*
import com.openclassrooms.realestatemanager.data.filter.FilterValue
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SetFilterUseCase @Inject constructor(
    private val getFilterUseCase: GetFilterUseCase,
    private val filterRepository: FilterRepository,
) {

    suspend fun applyRange(rangeToApply: Range<Float>) {
        val filterInfo = getFilterUseCase().first() as FilterInfo.Ranges

        filterRepository.applyFilter(
            filterToSet = filterInfo.type,
            filterToApply = if (rangeToApply != filterInfo.outerRange) {
                when (filterInfo.type) {
                    TYPE -> TODO()
                    PRICE -> FilterValue.DoubleMinMax(Range(rangeToApply.lower.toDouble(), rangeToApply.upper.toDouble()))
                    SURFACE -> FilterValue.IntMinMax(Range(rangeToApply.lower.toInt(), rangeToApply.upper.toInt()))
                    PHOTO_COUNT -> FilterValue.IntMinMax(Range(rangeToApply.lower.toInt(), rangeToApply.upper.toInt()))
                    POINT_OF_INTEREST -> TODO()
                    SALE_STATUS -> TODO()
                }
            } else {
                null
            },
        )
    }

    suspend fun applyEstateTypes(currentChoices: List<RealEstateType>) {
        val filterInfo = getFilterUseCase().first() as FilterInfo.MultiChoice.Type

        filterRepository.applyFilter(
            filterToSet = filterInfo.type,
            filterToApply = if (currentChoices.isNotEmpty()) {
                when (filterInfo.type) {
                    TYPE -> FilterValue.TypeChoice(currentChoices)
                    PRICE -> TODO()
                    SURFACE -> TODO()
                    PHOTO_COUNT -> TODO()
                    POINT_OF_INTEREST -> TODO()
                    SALE_STATUS -> TODO()
                }
            } else {
                null
            },
        )
    }

    suspend fun applyPoi(currentChoices: List<PointOfInterest>) {
        val filterInfo = getFilterUseCase().first() as FilterInfo.MultiChoice.Poi

        filterRepository.applyFilter(
            filterToSet = filterInfo.type,
            filterToApply = if (currentChoices.isNotEmpty()) {
                when (filterInfo.type) {
                    TYPE -> TODO()
                    PRICE -> TODO()
                    SURFACE -> TODO()
                    PHOTO_COUNT -> TODO()
                    POINT_OF_INTEREST -> FilterValue.PoiChoice(currentChoices)
                    SALE_STATUS -> TODO()
                }
            } else {
                null
            },
        )
    }

    fun applyDates(filterInfo: FilterInfo.Dates) {
        filterRepository.applyFilter(
            SALE_STATUS,
            if (filterInfo.availableEstates != null) {
                FilterValue.DateMinMax(
                    availableEstates = filterInfo.availableEstates,
                    min = filterInfo.min,
                    max = filterInfo.max,
                )
            } else {
                null
            }
        )
    }

    fun reset(filterType: FilterType) {
        filterRepository.applyFilter(filterToSet = filterType, filterToApply = null)
    }
}