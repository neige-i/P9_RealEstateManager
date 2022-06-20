package com.openclassrooms.realestatemanager.data.filter

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FilterRepository @Inject constructor() {

    // CAUTION: do NOT use MUTABLE collections with StateFlow
    // By default, no filter is applied
    private val appliedFiltersMutableStateFlow = MutableStateFlow<Map<FilterType, FilterValue>>(emptyMap())

    fun getAppliedFiltersFlow(): Flow<Map<FilterType, FilterValue>> = appliedFiltersMutableStateFlow

    fun apply(filterToSet: FilterType, filterToApply: FilterValue) {
        modifyFilter { put(filterToSet, filterToApply) }
    }

    fun clear(filterType: FilterType) {
        modifyFilter { remove(filterType) }
    }

    private fun modifyFilter(modification: MutableMap<FilterType, FilterValue>.() -> Unit) {
        appliedFiltersMutableStateFlow.update { allFilters ->
            allFilters.toMutableMap().apply { modification(this) }
        }
    }
}