package com.openclassrooms.realestatemanager.domain.filter

import com.openclassrooms.realestatemanager.data.UtilsRepository
import com.openclassrooms.realestatemanager.data.filter.FilterRepository
import com.openclassrooms.realestatemanager.data.filter.FilterType.*
import com.openclassrooms.realestatemanager.data.filter.FilterValue
import com.openclassrooms.realestatemanager.data.real_estate.RealEstateEntity
import com.openclassrooms.realestatemanager.data.real_estate.RealEstateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetFilteredEstatesUseCase @Inject constructor(
    private val filterRepository: FilterRepository,
    private val realEstateRepository: RealEstateRepository,
    private val utilsRepository: UtilsRepository,
) {

    operator fun invoke(): Flow<List<RealEstateEntity>> = combine(
        filterRepository.getAppliedFiltersFlow(),
        realEstateRepository.getAllRealEstates(),
    ) { appliedFilters, allEstates ->

        var filteredList = allEstates

        appliedFilters.forEach { (filterType, filterValue) ->
            if (filterValue == null) {
                return@forEach
            }
            filteredList = filteredList.filter { realEstate ->
                when (filterType) {
                    TYPE -> {
                        val typeRangeFilter = filterValue as FilterValue.TypeChoice
                        typeRangeFilter.value.map { it.name }.contains(realEstate.info.type)
                    }
                    PRICE -> {
                        val doubleRangeFilter = filterValue as FilterValue.DoubleMinMax
                        realEstate.info.price != null && realEstate.info.price in doubleRangeFilter.value
                    }
                    SURFACE -> {
                        val intRangeFilter = filterValue as FilterValue.IntMinMax
                        realEstate.info.area != null && realEstate.info.area in intRangeFilter.value
                    }
                    PHOTO_COUNT -> {
                        val intRangeFilter = filterValue as FilterValue.IntMinMax
                        realEstate.photoList.size in intRangeFilter.value
                    }
                    POINT_OF_INTEREST -> {
                        val poiRangeFilter = filterValue as FilterValue.PoiChoice
                        val poiValues = realEstate.poiList.map { it.poiValue }
                        poiRangeFilter.value.map { it.name }.any { poiValues.contains(it) }
                    }
                    SALE_STATUS -> {
                        val dateRangeFilter = filterValue as FilterValue.DateMinMax

                        val entryDate = utilsRepository.stringToDate(realEstate.info.marketEntryDate)
                        val saleDate = realEstate.info.saleDate?.let { utilsRepository.stringToDate(it) }

                        val availableFilter = if (dateRangeFilter.availableEstates) {
                            saleDate == null
                        } else {
                            saleDate != null
                        }

                        val dateToCheck = if (dateRangeFilter.availableEstates) {
                            entryDate
                        } else {
                            saleDate
                        }
                        val minFilter = if (dateRangeFilter.min != null) {
                            dateToCheck != null && !dateToCheck.isBefore(dateRangeFilter.min)
                        } else {
                            true
                        }
                        val maxFilter = if (dateRangeFilter.max != null) {
                            dateToCheck != null && !dateToCheck.isAfter(dateRangeFilter.max)
                        } else {
                            true
                        }

                        availableFilter && minFilter && maxFilter
                    }
                }
            }
        }
        filteredList
    }
}