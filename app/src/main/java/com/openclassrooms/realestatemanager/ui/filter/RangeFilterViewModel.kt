package com.openclassrooms.realestatemanager.ui.filter

import android.app.Application
import android.util.Range
import androidx.core.util.toRange
import androidx.lifecycle.*
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.filter.FilterType
import com.openclassrooms.realestatemanager.data.filter.FilterType.*
import com.openclassrooms.realestatemanager.domain.filter.FilterInfo
import com.openclassrooms.realestatemanager.domain.filter.GetFilterUseCase
import com.openclassrooms.realestatemanager.domain.filter.SetFilterUseCase
import com.openclassrooms.realestatemanager.ui.util.CoroutineProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RangeFilterViewModel @Inject constructor(
    getFilterUseCase: GetFilterUseCase,
    private val setFilterUseCase: SetFilterUseCase,
    private val coroutineProvider: CoroutineProvider,
    private val application: Application,
) : ViewModel() {

    companion object {
        private val RESET_RANGE = (-1f..-1f).toRange()
    }

    private val rangeFilterInfoLiveData: LiveData<FilterInfo.Ranges> = getFilterUseCase()
        .map { it as FilterInfo.Ranges }
        .asLiveData(coroutineProvider.getIoDispatcher())
    private val currentRangeMutableLiveData = MutableLiveData<Range<Float>>()

    private val viewStateMediatorLiveData = MediatorLiveData<RangeViewState>()
    val viewState: LiveData<RangeViewState> = viewStateMediatorLiveData

    init {
        viewStateMediatorLiveData.addSource(rangeFilterInfoLiveData) { filterInfo ->
            combineLiveData(filterInfo, currentRangeMutableLiveData.value)
        }
        viewStateMediatorLiveData.addSource(currentRangeMutableLiveData) { currentRange ->
            combineLiveData(rangeFilterInfoLiveData.value, currentRange)
        }
    }

    fun onSliderValuesChanged(currentRange: Range<Float>) {
        currentRangeMutableLiveData.value = currentRange
    }

    private fun combineLiveData(rangeFilterInfo: FilterInfo.Ranges?, currentRange: Range<Float>?) {
        if (rangeFilterInfo == null) {
            return
        }

        val cursorRange: Range<Float> = when (currentRange) {
            null -> rangeFilterInfo.innerRange
            RESET_RANGE -> rangeFilterInfo.outerRange
            else -> currentRange
        }

        viewStateMediatorLiveData.value = RangeViewState(
            dialogTitle = application.getString(
                when (rangeFilterInfo.type) {
                    TYPE -> TODO()
                    PRICE -> R.string.filter_price_dialog_title
                    SURFACE -> R.string.filter_surface_dialog_title
                    PHOTO_COUNT -> R.string.filter_photo_dialog_title
                    POINT_OF_INTEREST -> TODO()
                    SALE_STATUS -> TODO()
                }
            ),
            label = getFilterLabel(
                filterType = rangeFilterInfo.type,
                from = cursorRange.lower,
                to = cursorRange.upper
            ),
            from = rangeFilterInfo.outerRange.lower,
            to = rangeFilterInfo.outerRange.upper,
            min = cursorRange.lower,
            max = cursorRange.upper,
            step = rangeFilterInfo.step,
        )
    }

    private fun getFilterLabel(filterType: FilterType, from: Float, to: Float): String {
        return application.getString(
            when (filterType) {
                TYPE -> TODO()
                PRICE -> R.string.filter_price_range
                SURFACE -> R.string.filter_surface_range
                PHOTO_COUNT -> R.string.filter_photo_count_range
                POINT_OF_INTEREST -> TODO()
                SALE_STATUS -> TODO()
            },
            from.toInt(),
            to.toInt(),
        )
    }

    fun onPositiveButtonClicked() {
        val currentRange = currentRangeMutableLiveData.value ?: return

        viewModelScope.launch(coroutineProvider.getIoDispatcher()) {
            setFilterUseCase.applyRange(currentRange)
        }
    }

    fun onNeutralButtonClicked() {
        currentRangeMutableLiveData.value = RESET_RANGE
    }
}