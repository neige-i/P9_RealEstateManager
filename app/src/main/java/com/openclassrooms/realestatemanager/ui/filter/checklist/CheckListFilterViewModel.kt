package com.openclassrooms.realestatemanager.ui.filter.checklist

import androidx.lifecycle.*
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.Localized
import com.openclassrooms.realestatemanager.data.PointOfInterest
import com.openclassrooms.realestatemanager.data.RealEstateType
import com.openclassrooms.realestatemanager.data.filter.FilterRepository
import com.openclassrooms.realestatemanager.data.filter.FilterType
import com.openclassrooms.realestatemanager.data.filter.FilterValue
import com.openclassrooms.realestatemanager.data.real_estate.RealEstateRepository
import com.openclassrooms.realestatemanager.ui.filter.FilterViewModel
import com.openclassrooms.realestatemanager.ui.util.CoroutineProvider
import com.openclassrooms.realestatemanager.ui.util.update
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class CheckListFilterViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    filterRepository: FilterRepository,
    realEstateRepository: RealEstateRepository,
    coroutineProvider: CoroutineProvider,
) : FilterViewModel<FilterType.CheckList, FilterValue.Choices>(savedStateHandle, filterRepository, realEstateRepository) {

    private val checkedItemsMutableLiveData = MutableLiveData<MutableList<Localized>>()

    private val viewStateMediatorLiveData = MediatorLiveData<CheckListViewState>()
    val viewState: LiveData<CheckListViewState> = viewStateMediatorLiveData

    init {
        // Init checklist's selected items

        checkedItemsMutableLiveData.value = filterValue?.selectedItems?.toMutableList()
            ?: mutableListOf()

        // Setup view state's data sources

        val allItemsLiveData = realEstateRepository.getAllRealEstates()
            .map { estateList ->
                estateList
                    .asSequence() // Use Sequence for better performance
                    .let { allEstates ->
                        when (filterType) {
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
            .asLiveData(coroutineProvider.getIoDispatcher())

        viewStateMediatorLiveData.addSource(checkedItemsMutableLiveData) { checkedItems ->
            combineViewState(checkedItems, allItemsLiveData.value)
        }
        viewStateMediatorLiveData.addSource(allItemsLiveData) { allItems ->
            combineViewState(checkedItemsMutableLiveData.value, allItems)
        }
    }

    private fun combineViewState(checkedItems: List<Localized>?, allItems: Set<Localized>?) {
        if (checkedItems == null || allItems == null) {
            return
        }

        viewStateMediatorLiveData.value = CheckListViewState(
            dialogTitle = when (filterType) {
                is FilterType.EstateType -> R.string.filter_type_dialog_title
                is FilterType.PointOfInterest -> R.string.filter_poi_dialog_title
            },
            items = allItems.map { item ->
                CheckListViewState.CheckItem(
                    label = item.stringId,
                    isChecked = checkedItems.contains(item),
                    onClicked = { isChecked ->
                        checkedItemsMutableLiveData.update { currentCheckedItems ->
                            currentCheckedItems?.apply {
                                if (isChecked) add(item) else remove(item)
                            } ?: mutableListOf()
                        }
                    }
                )
            },
        )
    }

    override fun getFilterToApply(): FilterValue.Choices? {
        val checkedItems = checkedItemsMutableLiveData.value

        // The filter is not considered if the check list is empty
        if (checkedItems.isNullOrEmpty()) return null

        return when (filterType) {
            FilterType.EstateType -> FilterValue.EstateType(checkedItems.map { it as RealEstateType })
            FilterType.PointOfInterest -> FilterValue.Poi(checkedItems.map { it as PointOfInterest })
        }
    }

    override fun onNeutralButtonClicked() {
        checkedItemsMutableLiveData.value = mutableListOf()
    }
}