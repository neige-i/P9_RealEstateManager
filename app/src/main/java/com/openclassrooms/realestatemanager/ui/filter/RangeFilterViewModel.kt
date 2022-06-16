package com.openclassrooms.realestatemanager.ui.filter

import android.app.Application
import android.util.Range
import androidx.lifecycle.*
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.filter.FilterRepository
import com.openclassrooms.realestatemanager.data.filter.FilterType
import com.openclassrooms.realestatemanager.data.filter.FilterValue
import com.openclassrooms.realestatemanager.domain.filter.SetFilterUseCase
import com.openclassrooms.realestatemanager.domain.real_estate.GetAvailableValuesUseCase
import com.openclassrooms.realestatemanager.ui.util.CoroutineProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RangeFilterViewModel @Inject constructor(
    getAvailableValuesUseCase: GetAvailableValuesUseCase,
    private val setFilterUseCase: SetFilterUseCase,
    private val coroutineProvider: CoroutineProvider,
    private val application: Application,
    savedStateHandle: SavedStateHandle,
) : FilterViewModel<FilterType.Slider, FilterValue.MinMax<*>>(savedStateHandle) {

    private val sliderSelectionMutableLiveData = MutableLiveData<Range<Float>?>()

    private val sliderBoundsFlow: Flow<Range<Float>> = getAvailableValuesUseCase.getRangeBounds(filterType)

    private val viewStateMediatorLiveData = MediatorLiveData<RangeDialogViewState>()
    val viewState: LiveData<RangeDialogViewState> = viewStateMediatorLiveData

    init {
        // Init slider's selection

        sliderSelectionMutableLiveData.value = filterValue?.let { Range(it.min.toFloat(), it.max.toFloat()) }

        // Setup view state's data sources

        val filterBoundsLiveData = sliderBoundsFlow.asLiveData(coroutineProvider.getIoDispatcher())

        viewStateMediatorLiveData.addSource(sliderSelectionMutableLiveData) { sliderSelection ->
            combineViewState(sliderSelection, filterBoundsLiveData.value)
        }
        viewStateMediatorLiveData.addSource(filterBoundsLiveData) { sliderBounds ->
            combineViewState(sliderSelectionMutableLiveData.value, sliderBounds)
        }
    }

    private fun combineViewState(sliderSelection: Range<Float>?, sliderBounds: Range<Float>?) {
        if (sliderBounds == null) {
            return
        }

        val selectionRange: Range<Float> = sliderSelection ?: sliderBounds

        viewStateMediatorLiveData.value = RangeDialogViewState(
            style = when (filterType) {
                is FilterType.PhotoCount -> RangeDialogViewState.Style(
                    title = R.string.filter_photo_dialog_title,
                    label = application.getString(R.string.filter_photo_count_range, selectionRange.lower.toInt(), selectionRange.upper.toInt()),
                    step = FilterRepository.PHOTO_COUNT_RANGE_STEP,
                )
                is FilterType.Price -> RangeDialogViewState.Style(
                    title = R.string.filter_price_dialog_title,
                    label = application.getString(R.string.filter_price_range, selectionRange.lower.toInt(), selectionRange.upper.toInt()),
                    step = FilterRepository.PRICE_RANGE_STEP,
                )
                is FilterType.Surface -> RangeDialogViewState.Style(
                    title = R.string.filter_surface_dialog_title,
                    label = application.getString(R.string.filter_surface_range, selectionRange.lower.toInt(), selectionRange.upper.toInt()),
                    step = FilterRepository.SURFACE_RANGE_STEP,
                )
            },
            selection = selectionRange,
            bounds = sliderBounds,
        )
    }

    fun onSliderValuesChanged(currentRange: Range<Float>) {
        sliderSelectionMutableLiveData.value = currentRange
    }

    override fun onPositiveButtonClicked() {
        viewModelScope.launch(coroutineProvider.getIoDispatcher()) {
            setFilterUseCase.applyFilter(filterType, sliderSelectionMutableLiveData.value, sliderBoundsFlow.first())
        }
    }

    override fun onNeutralButtonClicked() {
        sliderSelectionMutableLiveData.value = null
    }
}