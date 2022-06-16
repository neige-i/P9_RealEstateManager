package com.openclassrooms.realestatemanager.ui.filter

import androidx.annotation.StringRes
import androidx.lifecycle.*
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.filter.FilterType
import com.openclassrooms.realestatemanager.data.filter.FilterValue
import com.openclassrooms.realestatemanager.domain.filter.SetFilterUseCase
import com.openclassrooms.realestatemanager.domain.real_estate.GetAvailableValuesUseCase
import com.openclassrooms.realestatemanager.ui.util.CoroutineProvider
import com.openclassrooms.realestatemanager.ui.util.update
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class MultiChoiceFilterViewModel @Inject constructor(
    getAvailableValuesUseCase: GetAvailableValuesUseCase,
    private val setFilterUseCase: SetFilterUseCase,
    coroutineProvider: CoroutineProvider,
    savedStateHandle: SavedStateHandle,
) : FilterViewModel<FilterType.CheckList, FilterValue.Choices>(savedStateHandle) {

    private val checkedItemsMutableLiveData = MutableLiveData<MutableList<Int>>()

    private val allItemsFlow: Flow<List<Int>> = when (filterType) {
        is FilterType.EstateType -> getAvailableValuesUseCase.getTypeList().map { it.map { it.labelId } }
        is FilterType.PointOfInterest -> getAvailableValuesUseCase.getPoiList().map { it.map { it.labelId } }
    }

    private val viewStateMediatorLiveData = MediatorLiveData<MultiChoiceViewState>()
    val viewState: LiveData<MultiChoiceViewState> = viewStateMediatorLiveData

    init {
        // Init checklist's selected items

        checkedItemsMutableLiveData.value = when (filterValue) {
            is FilterValue.EstateType -> filterValue.selectedEstateTypes.map { it.labelId }
            is FilterValue.Poi -> filterValue.selectedPois.map { it.labelId }
            null -> emptyList()
        }.toMutableList()

        // Setup view state's data sources

        val allItemsLiveData = allItemsFlow.asLiveData(coroutineProvider.getIoDispatcher())

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

        viewStateMediatorLiveData.value = MultiChoiceViewState(
            dialogTitle = when (filterType) {
                is FilterType.EstateType -> R.string.filter_type_dialog_title
                is FilterType.PointOfInterest -> R.string.filter_poi_dialog_title
            },
            checkItems = allItems.map { item ->
                MultiChoiceViewState.CheckItem(
                    label = item,
                    isChecked = checkedItems.contains(item),
                    onClicked = { isChecked -> onItemClicked(item, isChecked) }
                )
            },
        )
    }

    private fun onItemClicked(@StringRes itemId: Int, isChecked: Boolean) {
        checkedItemsMutableLiveData.update { currentCheckedItems ->
            currentCheckedItems?.apply {
                if (isChecked) add(itemId) else remove(itemId)
            } ?: mutableListOf()
        }
    }

    override fun onPositiveButtonClicked() {
        setFilterUseCase.applyFilter(filterType, checkedItemsMutableLiveData.value)
    }

    override fun onNeutralButtonClicked() {
        checkedItemsMutableLiveData.value = mutableListOf()
    }
}