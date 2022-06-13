package com.openclassrooms.realestatemanager.ui.filter

import android.app.Application
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
    private val application: Application,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val checkListType = savedStateHandle.get<FilterType.CheckList>(MultiChoiceFilterDialog.KEY_FILTER_TYPE)
        ?: throw IllegalStateException("The filter type must be passed as an argument to the Fragment")

    private val checkedItemsMutableLiveData = MutableLiveData<MutableList<String>>()

    private val allItemsFlow: Flow<List<String>> = when (checkListType) {
        is FilterType.EstateType -> getAvailableValuesUseCase.getTypeList().map { it.map { application.getString(it.labelId) } }
        is FilterType.PointOfInterest -> getAvailableValuesUseCase.getPoiList().map { it.map { application.getString(it.labelId) } }
    }

    private val viewStateMediatorLiveData = MediatorLiveData<MultiChoiceViewState>()
    val viewState: LiveData<MultiChoiceViewState> = viewStateMediatorLiveData

    init {
        // Init checklist's selected items

        val choicesFilterValue = savedStateHandle.get<FilterValue.Choices?>(MultiChoiceFilterDialog.KEY_FILTER_VALUE)
        checkedItemsMutableLiveData.value = when (choicesFilterValue) {
            is FilterValue.EstateType -> choicesFilterValue.selectedEstateTypes.map { application.getString(it.labelId) }
            is FilterValue.Poi -> choicesFilterValue.selectedPois.map { application.getString(it.labelId) }
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

    private fun combineViewState(checkedItems: List<String>?, allItems: List<String>?) {
        if (checkedItems == null || allItems == null) {
            return
        }

        val checkBoxes = allItems.map { checkedItems.contains(it) }

        viewStateMediatorLiveData.value = MultiChoiceViewState(
            dialogTitle = when (checkListType) {
                is FilterType.EstateType -> R.string.filter_type_dialog_title
                is FilterType.PointOfInterest -> R.string.filter_poi_dialog_title
            },
            labels = allItems,
            checkedItems = checkBoxes,
        )
    }

    fun onItemClicked(label: String, isChecked: Boolean) {
        checkedItemsMutableLiveData.update { currentCheckedItems ->
            currentCheckedItems?.apply {
                if (isChecked) add(label) else remove(label)
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