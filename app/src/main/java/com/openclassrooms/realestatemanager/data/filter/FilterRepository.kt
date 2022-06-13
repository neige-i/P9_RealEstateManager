package com.openclassrooms.realestatemanager.data.filter

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FilterRepository @Inject constructor() {

    companion object {
        const val PRICE_RANGE_STEP = 50f
        const val SURFACE_RANGE_STEP = 5f
        const val PHOTO_COUNT_RANGE_STEP = 1f
    }

    private val currentFilterTypeMutableSharedFlow = MutableSharedFlow<FilterType>(replay = 1)

    // CAUTION: do NOT use MUTABLE collections with StateFlow
    private val allFiltersMutableStateFlow = MutableStateFlow<Map<FilterType, FilterValue?>>(
        // By default, no filter is applied
        mapOf(
            FilterType.EstateType to null,
            FilterType.Price to null,
            FilterType.Surface to null,
            FilterType.PhotoCount to null,
            FilterType.PointOfInterest to null,
            FilterType.SaleStatus to null,
        )
    )

    fun getCurrentFilterFlow(): Flow<FilterType> = currentFilterTypeMutableSharedFlow

    fun getAllFiltersFlow(): Flow<Map<FilterType, FilterValue?>> = allFiltersMutableStateFlow

    fun applyFilter(filterToSet: FilterType, filterToApply: FilterValue?) {
        allFiltersMutableStateFlow.update { allFilters ->
            allFilters.toMutableMap().apply {
                put(filterToSet, filterToApply)
            }
        }
    }
}