package com.openclassrooms.realestatemanager.ui.filter.slider

import android.util.Range
import androidx.lifecycle.*
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.filter.FilterRepository
import com.openclassrooms.realestatemanager.data.filter.FilterType
import com.openclassrooms.realestatemanager.data.filter.FilterValue
import com.openclassrooms.realestatemanager.data.real_estate.RealEstateRepository
import com.openclassrooms.realestatemanager.ui.filter.FilterViewModel
import com.openclassrooms.realestatemanager.ui.util.CoroutineProvider
import com.openclassrooms.realestatemanager.ui.util.LocalText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class SliderFilterViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    filterRepository: FilterRepository,
    realEstateRepository: RealEstateRepository,
    coroutineProvider: CoroutineProvider,
) : FilterViewModel<FilterType.Slider, FilterValue.MinMax>(savedStateHandle, filterRepository, realEstateRepository) {

    companion object {
        const val PRICE_RANGE_STEP = 50f
        const val SURFACE_RANGE_STEP = 5f
        const val PHOTO_COUNT_RANGE_STEP = 1f
    }

    private val sliderSelectionMutableLiveData = MutableLiveData<Range<Float>?>()

    private val sliderBoundsLiveData: LiveData<Range<Float>> = getEstateBound(Bound.MAX) { realEstate ->
        when (filterType) {
            FilterType.PhotoCount -> realEstate.photoList.size
            FilterType.Price -> realEstate.info.price
            FilterType.Surface -> realEstate.info.area
        }?.toFloat()
    }
        .map { maxValue -> Range(0f, adjustMaxValueWithSliderStep(maxValue)) }
        .asLiveData(coroutineProvider.getIoDispatcher())

    private val viewStateMediatorLiveData = MediatorLiveData<SliderViewState>()
    val viewState: LiveData<SliderViewState> = viewStateMediatorLiveData

    init {
        // Init slider's selection

        sliderSelectionMutableLiveData.value = filterValue?.let { it -> Range(it.min.toFloat(), it.max.toFloat()) }

        // Setup view state's data sources

        viewStateMediatorLiveData.addSource(sliderSelectionMutableLiveData) { sliderSelection ->
            combineViewState(sliderSelection, sliderBoundsLiveData.value)
        }
        viewStateMediatorLiveData.addSource(sliderBoundsLiveData) { sliderBounds ->
            combineViewState(sliderSelectionMutableLiveData.value, sliderBounds)
        }
    }

    /**
     * The slider's max value must be a multiple of the slider's step.
     */
    private fun adjustMaxValueWithSliderStep(maxValue: Float?): Float {
        val step = when (filterType) {
            FilterType.PhotoCount -> PHOTO_COUNT_RANGE_STEP
            FilterType.Price -> PRICE_RANGE_STEP
            FilterType.Surface -> SURFACE_RANGE_STEP
        }

        if (maxValue == null) {
            return step
        }

        val quotient = (maxValue / step).toInt()
        val remainder = maxValue % step

        // Return maxValue if it is already a multiple of step, otherwise, return the next step
        return if (remainder == 0f) maxValue else step * quotient.inc()
    }

    private fun combineViewState(sliderSelection: Range<Float>?, sliderBounds: Range<Float>?) {
        if (sliderBounds == null) {
            return
        }

        val selectionRange: Range<Float> = sliderSelection ?: sliderBounds
        val stringArgs = listOf(selectionRange.lower.toInt(), selectionRange.upper.toInt())

        viewStateMediatorLiveData.value = SliderViewState(
            style = when (filterType) {
                is FilterType.PhotoCount -> SliderViewState.Style(
                    dialogTitle = R.string.filter_photo_dialog_title,
                    label = LocalText.ResWithArgs(stringId = R.string.filter_photo_count_range, args = stringArgs),
                    step = PHOTO_COUNT_RANGE_STEP,
                )
                is FilterType.Price -> SliderViewState.Style(
                    dialogTitle = R.string.filter_price_dialog_title,
                    label = LocalText.ResWithArgs(stringId = R.string.filter_price_range, args = stringArgs),
                    step = PRICE_RANGE_STEP,
                )
                is FilterType.Surface -> SliderViewState.Style(
                    dialogTitle = R.string.filter_surface_dialog_title,
                    label = LocalText.ResWithArgs(stringId = R.string.filter_surface_range, args = stringArgs),
                    step = SURFACE_RANGE_STEP,
                )
            },
            selection = selectionRange,
            bounds = sliderBounds,
        )
    }

    fun onSliderValuesChanged(currentRange: Range<Float>) {
        sliderSelectionMutableLiveData.value = currentRange
    }

    override fun getFilterToApply(): FilterValue.MinMax? {
        val sliderSelection = sliderSelectionMutableLiveData.value
        val sliderBounds = sliderBoundsLiveData.value

        if (sliderSelection == null ||
            sliderBounds == null ||
            sliderSelection == sliderBounds
        ) {
            return null
        }

        return when (filterType) {
            is FilterType.PhotoCount -> FilterValue.PhotoCount(min = sliderSelection.lower.toInt(), max = sliderSelection.upper.toInt())
            is FilterType.Price -> FilterValue.Price(min = sliderSelection.lower.toDouble(), max = sliderSelection.upper.toDouble())
            is FilterType.Surface -> FilterValue.Surface(min = sliderSelection.lower.toInt(), max = sliderSelection.upper.toInt())
        }
    }

    override fun onNeutralButtonClicked() {
        sliderSelectionMutableLiveData.value = null
    }
}