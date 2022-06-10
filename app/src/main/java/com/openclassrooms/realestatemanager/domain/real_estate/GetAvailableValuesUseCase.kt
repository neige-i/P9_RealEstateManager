package com.openclassrooms.realestatemanager.domain.real_estate

import android.util.Range
import com.openclassrooms.realestatemanager.data.Localized
import com.openclassrooms.realestatemanager.data.filter.FilterRepository
import com.openclassrooms.realestatemanager.data.filter.FilterType
import com.openclassrooms.realestatemanager.data.real_estate.RealEstateEntity
import com.openclassrooms.realestatemanager.data.real_estate.RealEstateRepository
import com.openclassrooms.realestatemanager.ui.util.toLocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class GetAvailableValuesUseCase @Inject constructor(
    private val realEstateRepository: RealEstateRepository,
) {

    fun getSliderBounds(sliderFilter: FilterType.Slider): Flow<Range<Float>> = getEstateBound(Bound.MAX) { realEstate ->
        when (sliderFilter) {
            FilterType.PhotoCount -> realEstate.photoList.size
            FilterType.Price -> realEstate.info.price
            FilterType.Surface -> realEstate.info.area
        }?.toFloat()
    }.map { maxValue ->
        val step = when (sliderFilter) {
            FilterType.PhotoCount -> FilterRepository.PHOTO_COUNT_RANGE_STEP
            FilterType.Price -> FilterRepository.PRICE_RANGE_STEP
            FilterType.Surface -> FilterRepository.SURFACE_RANGE_STEP
        }

        val sliderMaxValue = if (maxValue != null) adjustForSlider(maxValue, step) else step

        Range(0f, sliderMaxValue)
    }

    /**
     * The slider's max value must be a multiple of the slider's step.
     */
    private fun adjustForSlider(maxValue: Float, step: Float): Float {
        val mod = maxValue % step
        return if (mod == 0f) maxValue else maxValue + step - mod
    }

    fun getMinEntryDate(): Flow<LocalDate?> = getEstateBound(Bound.MIN) { realEstate ->
        realEstate.info.marketEntryDate.toLocalDate()
    }

    fun getMaxSaleDate(): Flow<LocalDate?> = getEstateBound(Bound.MAX) { realEstate ->
        realEstate.info.saleDate?.toLocalDate()
    }

    private fun <T : Comparable<T>> getEstateBound(
        bound: Bound,
        map: (RealEstateEntity) -> T?
    ): Flow<T?> = realEstateRepository.getAllRealEstates().map { allEstates ->

        val mappedValues = allEstates.mapNotNull { realEstate -> map(realEstate) }

        when (bound) {
            Bound.MIN -> mappedValues.minOrNull()
            Bound.MAX -> mappedValues.maxOrNull()
        }
    }

    fun getSelectedItems(checkListFilter: FilterType.CheckList): Flow<Set<Localized>> = realEstateRepository.getAllRealEstates().map {
        it.asSequence() // Use Sequence for better performance
            .let { allEstates ->
                when (checkListFilter) {
                    FilterType.EstateType -> allEstates.map { realEstate ->
                        realEstate.info.type
                    }
                    FilterType.PointOfInterest -> allEstates.flatMap { realEstate ->
                        realEstate.poiList.map { poi -> poi.poiValue }
                    }
                }
            }
            .toSet() // To remove duplicates
    }

    enum class Bound {
        MIN,
        MAX,
    }
}