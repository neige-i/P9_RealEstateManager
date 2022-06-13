package com.openclassrooms.realestatemanager.domain.filter

import android.util.Range
import com.openclassrooms.realestatemanager.data.UtilsRepository
import com.openclassrooms.realestatemanager.data.filter.FilterRepository
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
        filterRepository.getAllFiltersFlow(),
        realEstateRepository.getAllRealEstates(),
    ) { allFilters, allEstates ->
        allEstates.filter { realEstate -> isCorrect(realEstate, allFilters.values.filterNotNull()) }
    }

    private fun isCorrect(realEstate: RealEstateEntity, filterValues: Collection<FilterValue>): Boolean = filterValues.all { filterValue ->
        when (filterValue) {
            is FilterValue.EstateType -> filterValue.selectedEstateTypes
                .map { it.name }
                .contains(realEstate.info.type)
            is FilterValue.PhotoCount -> realEstate.photoList.size in filterValue.min..filterValue.max
            is FilterValue.Poi -> filterValue.selectedPois
                .map { poi -> poi.name }
                .any { poiName ->
                    realEstate.poiList
                        .map { poiEntity -> poiEntity.poiValue }
                        .contains(poiName)
                }
            is FilterValue.Price -> realEstate.info.price in Range(filterValue.min, filterValue.max)
            is FilterValue.AvailableDates -> {
                val entryDate = utilsRepository.stringToDate(realEstate.info.marketEntryDate)
                val saleDate = realEstate.info.saleDate?.let { utilsRepository.stringToDate(it) }

                val applyFilter = if (filterValue.availableEstates) {
                    saleDate == null
                } else {
                    saleDate != null
                }

                val dateToCheck = if (filterValue.availableEstates) entryDate else saleDate

                val minFilter = if (filterValue.from != null) {
                    dateToCheck != null && !dateToCheck.isBefore(filterValue.from)
                } else {
                    true
                }
                val maxFilter = if (filterValue.until != null) {
                    dateToCheck != null && !dateToCheck.isAfter(filterValue.until)
                } else {
                    true
                }

                applyFilter && minFilter && maxFilter
            }
            is FilterValue.Surface -> realEstate.info.area in filterValue.min..filterValue.max
        }
    }
}