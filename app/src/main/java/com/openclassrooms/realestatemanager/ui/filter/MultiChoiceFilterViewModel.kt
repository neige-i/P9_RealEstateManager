package com.openclassrooms.realestatemanager.ui.filter

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.PointOfInterest
import com.openclassrooms.realestatemanager.data.RealEstateType
import com.openclassrooms.realestatemanager.data.filter.FilterType
import com.openclassrooms.realestatemanager.domain.filter.FilterInfo
import com.openclassrooms.realestatemanager.domain.filter.GetFilterUseCase
import com.openclassrooms.realestatemanager.domain.filter.SetFilterUseCase
import com.openclassrooms.realestatemanager.ui.util.CoroutineProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MultiChoiceFilterViewModel @Inject constructor(
    getFilterUseCase: GetFilterUseCase,
    private val setFilterUseCase: SetFilterUseCase,
    private val coroutineProvider: CoroutineProvider,
    private val application: Application,
) : ViewModel() {

    private val multiChoiceFilterInfoLiveData: LiveData<FilterInfo.MultiChoice<*>> =
        getFilterUseCase()
            .map { filterInfo -> filterInfo as FilterInfo.MultiChoice<*> }
            .asLiveData(coroutineProvider.getIoDispatcher())
    private val currentCheckedItemsMediatorLiveData = MediatorLiveData<List<String>>().apply {
        addSource(multiChoiceFilterInfoLiveData) {
            value = when (it) {
                is FilterInfo.MultiChoice.Type -> it.selection.map { application.getString(it.labelId) }
                is FilterInfo.MultiChoice.Poi -> it.selection.map { application.getString(it.labelId) }
            }
        }
    }

    private val viewStateMediatorLiveData = MediatorLiveData<MultiChoiceViewState>()
    val viewState: LiveData<MultiChoiceViewState> = viewStateMediatorLiveData

    init {
        viewStateMediatorLiveData.addSource(multiChoiceFilterInfoLiveData) { multiChoiceFilterInfo ->
            combineViewState(multiChoiceFilterInfo, currentCheckedItemsMediatorLiveData.value)
        }
        viewStateMediatorLiveData.addSource(currentCheckedItemsMediatorLiveData) { currentCheckedItems ->
            combineViewState(multiChoiceFilterInfoLiveData.value, currentCheckedItems)
        }
    }

    private fun combineViewState(
        multiChoiceFilterInfo: FilterInfo.MultiChoice<*>?,
        currentCheckedItems: List<String>?,
    ) {
        if (multiChoiceFilterInfo == null || currentCheckedItems == null) {
            return
        }

        val allTypes = RealEstateType.values()
        val allPois = PointOfInterest.values()

        viewStateMediatorLiveData.value = MultiChoiceViewState(
            dialogTitle = application.getString(
                when (multiChoiceFilterInfo.type) {
                    FilterType.TYPE -> R.string.filter_type_dialog_title
                    FilterType.PRICE -> TODO()
                    FilterType.SURFACE -> TODO()
                    FilterType.PHOTO_COUNT -> TODO()
                    FilterType.POINT_OF_INTEREST -> R.string.filter_poi_dialog_title
                    FilterType.SALE_STATUS -> TODO()
                }
            ),
            labels = when (multiChoiceFilterInfo) {
                is FilterInfo.MultiChoice.Type -> {
                    allTypes.map { application.getString(it.labelId) }
                }
                is FilterInfo.MultiChoice.Poi -> {
                    allPois.map { application.getString(it.labelId) }
                }
            },
            checkedItems = when (multiChoiceFilterInfo) {
                is FilterInfo.MultiChoice.Type -> {
                    allTypes.map { currentCheckedItems.contains(application.getString(it.labelId)) }
                }
                is FilterInfo.MultiChoice.Poi -> {
                    allPois.map { currentCheckedItems.contains(application.getString(it.labelId)) }
                }
            },
        )
    }

    fun onItemClicked(index: Int, isChecked: Boolean) {
        val whichMultiChoice = multiChoiceFilterInfoLiveData.value ?: return
        val currentCheckedItems = currentCheckedItemsMediatorLiveData.value?.toMutableList()
            ?: return

        val clickedItem = application.getString(
            when (whichMultiChoice) {
                is FilterInfo.MultiChoice.Type -> RealEstateType.values()[index].labelId
                is FilterInfo.MultiChoice.Poi -> PointOfInterest.values()[index].labelId
            }
        )
        if (isChecked) {
            currentCheckedItems.add(clickedItem)
        } else {
            currentCheckedItems.remove(clickedItem)
        }

        currentCheckedItemsMediatorLiveData.value = currentCheckedItems
    }

    fun onPositiveButtonClicked() {
        val whichMultiChoice = multiChoiceFilterInfoLiveData.value ?: return
        val currentCheckedItems = currentCheckedItemsMediatorLiveData.value ?: return

        val newSelection = currentCheckedItems.map { item ->
            when (whichMultiChoice) {
                is FilterInfo.MultiChoice.Type -> RealEstateType.fromLocaleString(item, application)
                is FilterInfo.MultiChoice.Poi -> PointOfInterest.values()
                    .first { application.getString(it.labelId) == item }
            }
        }

        viewModelScope.launch(coroutineProvider.getIoDispatcher()) {
            when (whichMultiChoice) {
                is FilterInfo.MultiChoice.Type -> setFilterUseCase.applyEstateTypes(newSelection as List<RealEstateType>)
                is FilterInfo.MultiChoice.Poi -> setFilterUseCase.applyPoi(newSelection as List<PointOfInterest>)
            }
        }
    }

    fun onNeutralButtonClicked() {
        currentCheckedItemsMediatorLiveData.value = emptyList()
    }
}