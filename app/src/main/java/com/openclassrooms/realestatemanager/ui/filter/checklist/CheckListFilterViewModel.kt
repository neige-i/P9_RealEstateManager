package com.openclassrooms.realestatemanager.ui.filter.checklist

import androidx.lifecycle.*
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.filter.FilterType
import com.openclassrooms.realestatemanager.data.filter.FilterValue
import com.openclassrooms.realestatemanager.domain.filter.SetFilterUseCase
import com.openclassrooms.realestatemanager.domain.real_estate.GetAvailableValuesUseCase
import com.openclassrooms.realestatemanager.ui.filter.FilterViewModel
import com.openclassrooms.realestatemanager.ui.util.CoroutineProvider
import com.openclassrooms.realestatemanager.ui.util.update
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class CheckListFilterViewModel @Inject constructor(
    getAvailableValuesUseCase: GetAvailableValuesUseCase,
    private val setFilterUseCase: SetFilterUseCase,
    coroutineProvider: CoroutineProvider,
    savedStateHandle: SavedStateHandle,
) : FilterViewModel<FilterType.CheckList, FilterValue.Choices>(savedStateHandle) {

    private val checkedItemsMutableLiveData = MutableLiveData<MutableList<Int>>()

    private val viewStateMediatorLiveData = MediatorLiveData<CheckListViewState>()
    val viewState: LiveData<CheckListViewState> = viewStateMediatorLiveData

    init {
        // Init checklist's selected items

        checkedItemsMutableLiveData.value = when (filterValue) {
            is FilterValue.EstateType -> filterValue.selectedEstateTypes.map { it.labelId }
            is FilterValue.Poi -> filterValue.selectedPois.map { it.labelId }
            null -> emptyList()
        }.toMutableList()

        // Setup view state's data sources

        val allItemsLiveData = when (filterType) {
            is FilterType.EstateType -> getAvailableValuesUseCase.getTypeList().map { it.map { it.labelId } }
            is FilterType.PointOfInterest -> getAvailableValuesUseCase.getPoiList().map { it.map { it.labelId } }
        }.asLiveData(coroutineProvider.getIoDispatcher())

        viewStateMediatorLiveData.addSource(checkedItemsMutableLiveData) { checkedItems ->
            combineViewState(checkedItems, allItemsLiveData.value)
        }
        viewStateMediatorLiveData.addSource(allItemsLiveData) { allItems ->
            combineViewState(checkedItemsMutableLiveData.value, allItems)
        }
    }

    private fun combineViewState(checkedItems: List<Int>?, allItems: List<Int>?) {
        if (checkedItems == null || allItems == null) {
            return
        }

        viewStateMediatorLiveData.value = CheckListViewState(
            dialogTitle = when (filterType) {
                is FilterType.EstateType -> R.string.filter_type_dialog_title
                is FilterType.PointOfInterest -> R.string.filter_poi_dialog_title
            },
            items = allItems.map { itemId ->
                CheckListViewState.CheckItem(
                    label = itemId,
                    isChecked = checkedItems.contains(itemId),
                    onClicked = { isChecked ->
                        checkedItemsMutableLiveData.update { currentCheckedItems ->
                            currentCheckedItems?.apply {
                                if (isChecked) add(itemId) else remove(itemId)
                            } ?: mutableListOf()
                        }
                    }
                )
            },
        )
    }

    override fun onPositiveButtonClicked() {
        setFilterUseCase.applyFilter(filterType, checkedItemsMutableLiveData.value)
    }

    override fun onNeutralButtonClicked() {
        checkedItemsMutableLiveData.value = mutableListOf()
    }
}