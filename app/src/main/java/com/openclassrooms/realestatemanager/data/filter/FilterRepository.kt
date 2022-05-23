package com.openclassrooms.realestatemanager.data.filter

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
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

    // CAUTION: do NOT use StateFlow with mutable collection
    private val appliedFiltersMutableSharedFlow =
        MutableSharedFlow<MutableMap<FilterType, FilterValue?>>(replay = 1).apply {
            tryEmit(
                FilterType.values()
                    .associateWith { null } // By default, no filter is applied
                    .toMutableMap()
            )
        }

    fun getCurrentFilterFlow(): Flow<FilterType> = currentFilterTypeMutableSharedFlow

    fun setCurrentFilter(filterType: FilterType) {
        currentFilterTypeMutableSharedFlow.tryEmit(filterType)
    }

    fun getAppliedFiltersFlow(): Flow<Map<FilterType, FilterValue?>> =
        appliedFiltersMutableSharedFlow

    fun applyFilter(filterToSet: FilterType, filterToApply: FilterValue?) {
        appliedFiltersMutableSharedFlow.tryEmit(
            appliedFiltersMutableSharedFlow.replayCache.first().apply {
                set(filterToSet, filterToApply)
            }
        )
    }
}