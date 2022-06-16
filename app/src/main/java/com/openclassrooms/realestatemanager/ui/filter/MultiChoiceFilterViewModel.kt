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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MultiChoiceFilterViewModel @Inject constructor(
    getAvailableValuesUseCase: GetAvailableValuesUseCase,
    private val setFilterUseCase: SetFilterUseCase,
    private val coroutineProvider: CoroutineProvider,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val checkListType = savedStateHandle.get<FilterType.CheckList>(MultiChoiceFilterDialog.KEY_FILTER_TYPE)
        ?: throw IllegalStateException("The filter type must be passed as an argument to the Fragment")

    private val checkedItemsMutableLiveData = MutableLiveData<MutableList<Int>>()

    private val allItemsFlow: Flow<List<Int>> = when (checkListType) {
        is FilterType.EstateType -> getAvailableValuesUseCase.getTypeList().map { it.map { it.labelId } }
        is FilterType.PointOfInterest -> getAvailableValuesUseCase.getPoiList().map { it.map { it.labelId } }
    }

    private val viewStateMediatorLiveData = MediatorLiveData<MultiChoiceViewState>()
    val viewState: LiveData<MultiChoiceViewState> = viewStateMediatorLiveData

    init {
        // Init checklist's selected items

        val choicesFilterValue = savedStateHandle.get<FilterValue.Choices?>(MultiChoiceFilterDialog.KEY_FILTER_VALUE)
        checkedItemsMutableLiveData.value = when (choicesFilterValue) {
            is FilterValue.EstateType -> choicesFilterValue.selectedEstateTypes.map { it.labelId }
            is FilterValue.Poi -> choicesFilterValue.selectedPois.map { it.labelId }
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
            dialogTitle = when (checkListType) {
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

    fun onPositiveButtonClicked() {
        viewModelScope.launch(coroutineProvider.getIoDispatcher()) {
            setFilterUseCase.applyFilter(checkListType, checkedItemsMutableLiveData.value)
        }
    }

    fun onNeutralButtonClicked() {
        checkedItemsMutableLiveData.value = mutableListOf()
    }
}