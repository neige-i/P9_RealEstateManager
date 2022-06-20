package com.openclassrooms.realestatemanager.ui.filter

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.data.filter.FilterRepository
import com.openclassrooms.realestatemanager.data.filter.FilterType
import com.openclassrooms.realestatemanager.data.filter.FilterValue
import com.openclassrooms.realestatemanager.data.real_estate.RealEstateEntity
import com.openclassrooms.realestatemanager.data.real_estate.RealEstateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

abstract class FilterViewModel<out T : FilterType, out V : FilterValue>(
    savedStateHandle: SavedStateHandle,
    private val filterRepository: FilterRepository,
    private val realEstateRepository: RealEstateRepository,
) : ViewModel() {

    protected val filterType = savedStateHandle.get<T>(FilterDialog.KEY_FILTER_TYPE)
        ?: throw IllegalStateException("The filter type must be passed as an argument to the Fragment")
    protected val filterValue = savedStateHandle.get<V>(FilterDialog.KEY_FILTER_VALUE)

    protected fun <T : Comparable<T>> getEstateBound(
        bound: Bound,
        map: (RealEstateEntity) -> T?
    ): Flow<T?> = realEstateRepository.getAllRealEstates().map { allEstates ->

        val mappedValues = allEstates.mapNotNull { realEstate -> map(realEstate) }

        when (bound) {
            Bound.MIN -> mappedValues.minOrNull()
            Bound.MAX -> mappedValues.maxOrNull()
        }
    }

    fun onPositiveButtonClicked() {
        val filterToApply = getFilterToApply()

        if (filterToApply != null) {
            filterRepository.apply(filterType, filterToApply)
        } else {
            filterRepository.clear(filterType)
        }
    }

    protected abstract fun getFilterToApply(): V?

    abstract fun onNeutralButtonClicked()

    enum class Bound {
        MIN,
        MAX,
    }
}