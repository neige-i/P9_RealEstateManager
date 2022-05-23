package com.openclassrooms.realestatemanager.domain.filter

import android.util.Range
import com.openclassrooms.realestatemanager.data.UtilsRepository
import com.openclassrooms.realestatemanager.data.filter.FilterRepository
import com.openclassrooms.realestatemanager.data.filter.FilterRepository.Companion.PHOTO_COUNT_RANGE_STEP
import com.openclassrooms.realestatemanager.data.filter.FilterRepository.Companion.PRICE_RANGE_STEP
import com.openclassrooms.realestatemanager.data.filter.FilterRepository.Companion.SURFACE_RANGE_STEP
import com.openclassrooms.realestatemanager.data.filter.FilterType
import com.openclassrooms.realestatemanager.data.filter.FilterType.*
import com.openclassrooms.realestatemanager.data.filter.FilterValue
import com.openclassrooms.realestatemanager.data.real_estate.RealEstateEntity
import com.openclassrooms.realestatemanager.data.real_estate.RealEstateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetFilterUseCase @Inject constructor(
    private val filterRepository: FilterRepository,
    private val realEstateRepository: RealEstateRepository,
    private val utilsRepository: UtilsRepository,
) {

    operator fun invoke(): Flow<FilterInfo> {
        return combine(
            filterRepository.getCurrentFilterFlow(),
            filterRepository.getAppliedFiltersFlow(),
            realEstateRepository.getAllRealEstates(),
        ) { currentFilter, appliedFilters, allEstates ->

            val currentFilterValue = appliedFilters[currentFilter]
            when (currentFilter) {
                TYPE -> getTypeChoiceFilterInfo(
                    currentFilter,
                    currentFilterValue as FilterValue.TypeChoice?
                )
                PRICE -> geRangesFilterInfo(
                    currentFilter,
                    currentFilterValue as FilterValue.DoubleMinMax?,
                    allEstates
                )
                SURFACE, PHOTO_COUNT -> geRangesFilterInfo(
                    currentFilter,
                    currentFilterValue as FilterValue.IntMinMax?,
                    allEstates
                )
                POINT_OF_INTEREST -> getPoiChoiceFilterInfo(
                    currentFilter,
                    currentFilterValue as FilterValue.PoiChoice?
                )
                SALE_STATUS -> {
                    getRangesFilterInfo(
                        currentFilterValue as FilterValue.DateMinMax?,
                        allEstates,
                    )
                }
            }
        }
    }

    private fun geRangesFilterInfo(
        currentFilter: FilterType,
        appliedRangeFilterValue: FilterValue.IntMinMax?,
        allEstates: List<RealEstateEntity>,
    ): FilterInfo.Ranges {
        val limitMin = 0f
        val limitMax = allEstates
            .mapNotNull { realEstate ->
                when (currentFilter) {
                    TYPE -> TODO()
                    PRICE -> realEstate.info.price?.toInt()?.toFloat()
                    SURFACE -> realEstate.info.area?.toFloat()
                    PHOTO_COUNT -> realEstate.photoList.size.toFloat()
                    POINT_OF_INTEREST -> TODO()
                    SALE_STATUS -> TODO()
                }
            }
            .sorted()
            .maxOrNull()
            ?.let { maxValue ->
                // Range limits must be a factor of the step value
                when (currentFilter) {
                    TYPE -> TODO()
                    PRICE -> getRangedMaxLimit(maxValue, PRICE_RANGE_STEP)
                    SURFACE -> getRangedMaxLimit(maxValue, SURFACE_RANGE_STEP)
                    PHOTO_COUNT -> getRangedMaxLimit(maxValue, PHOTO_COUNT_RANGE_STEP)
                    POINT_OF_INTEREST -> TODO()
                    SALE_STATUS -> TODO()
                }
            }
            ?: 0f

        val (min, max) = if (appliedRangeFilterValue != null) {
            appliedRangeFilterValue.value.lower.toFloat() to appliedRangeFilterValue.value.upper.toFloat()
        } else {
            limitMin to limitMax
        }

        return FilterInfo.Ranges(
            type = currentFilter,
            outerRange = Range(limitMin, limitMax),
            innerRange = Range(min, max),
            step = when (currentFilter) {
                TYPE -> TODO()
                PRICE -> PRICE_RANGE_STEP
                SURFACE -> SURFACE_RANGE_STEP
                PHOTO_COUNT -> PHOTO_COUNT_RANGE_STEP
                POINT_OF_INTEREST -> TODO()
                SALE_STATUS -> TODO()
            }
        )
    }

    private fun geRangesFilterInfo(
        currentFilter: FilterType,
        appliedRangeFilterValue: FilterValue.DoubleMinMax?,
        allEstates: List<RealEstateEntity>,
    ): FilterInfo.Ranges {
        val limitMin = 0f
        val limitMax = allEstates
            .mapNotNull { realEstate ->
                when (currentFilter) {
                    TYPE -> TODO()
                    PRICE -> realEstate.info.price?.toInt()?.toFloat()
                    SURFACE -> realEstate.info.area?.toFloat()
                    PHOTO_COUNT -> realEstate.photoList.size.toFloat()
                    POINT_OF_INTEREST -> TODO()
                    SALE_STATUS -> TODO()
                }
            }
            .sorted()
            .maxOrNull()
            ?.let { maxValue ->
                // Range limits must be a factor of the step value
                when (currentFilter) {
                    TYPE -> TODO()
                    PRICE -> getRangedMaxLimit(maxValue, PRICE_RANGE_STEP)
                    SURFACE -> getRangedMaxLimit(maxValue, SURFACE_RANGE_STEP)
                    PHOTO_COUNT -> getRangedMaxLimit(maxValue, PHOTO_COUNT_RANGE_STEP)
                    POINT_OF_INTEREST -> TODO()
                    SALE_STATUS -> TODO()
                }
            }
            ?: 0f

        val (min, max) = if (appliedRangeFilterValue != null) {
            appliedRangeFilterValue.value.lower.toFloat() to appliedRangeFilterValue.value.upper.toFloat()
        } else {
            limitMin to limitMax
        }

        return FilterInfo.Ranges(
            type = currentFilter,
            outerRange = Range(limitMin, limitMax),
            innerRange = Range(min, max),
            step = when (currentFilter) {
                TYPE -> TODO()
                PRICE -> PRICE_RANGE_STEP
                SURFACE -> SURFACE_RANGE_STEP
                PHOTO_COUNT -> PHOTO_COUNT_RANGE_STEP
                POINT_OF_INTEREST -> TODO()
                SALE_STATUS -> TODO()
            }
        )
    }

    private fun getRangesFilterInfo(
        appliedRangeFilterValue: FilterValue.DateMinMax?,
        allEstates: List<RealEstateEntity>,
    ): FilterInfo.Dates {
        return FilterInfo.Dates(
            minDateLimit = allEstates.minOfOrNull { realEstate ->
                utilsRepository.stringToDate(realEstate.info.marketEntryDate)
            },
            maxDateLimit = allEstates.mapNotNull { realEstate ->
                realEstate.info.saleDate?.let { utilsRepository.stringToDate(it) }
            }.maxOrNull(),
            min = appliedRangeFilterValue?.min,
            max = appliedRangeFilterValue?.max,
            availableEstates = appliedRangeFilterValue?.availableEstates,
        )
    }

    private fun getRangedMaxLimit(maxValue: Float, step: Float): Float {
        val mod = maxValue % step
        return if (mod == 0f) maxValue else maxValue + step - mod
    }

    private fun getTypeChoiceFilterInfo(
        currentFilter: FilterType,
        appliedTypeChoiceFilterValue: FilterValue.TypeChoice?,
    ): FilterInfo.MultiChoice.Type {
        return FilterInfo.MultiChoice.Type(
            type = currentFilter,
            selection = appliedTypeChoiceFilterValue?.value ?: emptyList(),
        )
    }

    private fun getPoiChoiceFilterInfo(
        currentFilter: FilterType,
        appliedTypeChoiceFilterValue: FilterValue.PoiChoice?,
    ): FilterInfo.MultiChoice.Poi {
        return FilterInfo.MultiChoice.Poi(
            type = currentFilter,
            selection = appliedTypeChoiceFilterValue?.value ?: emptyList(),
        )
    }
}