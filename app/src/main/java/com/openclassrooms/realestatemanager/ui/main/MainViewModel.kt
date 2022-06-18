package com.openclassrooms.realestatemanager.ui.main

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.ResourcesRepository
import com.openclassrooms.realestatemanager.data.UtilsRepository
import com.openclassrooms.realestatemanager.data.filter.FilterRepository
import com.openclassrooms.realestatemanager.data.filter.FilterType
import com.openclassrooms.realestatemanager.data.filter.FilterType.*
import com.openclassrooms.realestatemanager.data.filter.FilterValue
import com.openclassrooms.realestatemanager.data.real_estate.CurrentEstateRepository
import com.openclassrooms.realestatemanager.domain.form.FormType
import com.openclassrooms.realestatemanager.domain.form.SetFormUseCase
import com.openclassrooms.realestatemanager.ui.util.CoroutineProvider
import com.openclassrooms.realestatemanager.ui.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    filterRepository: FilterRepository,
    private val currentEstateRepository: CurrentEstateRepository,
    private val setFormUseCase: SetFormUseCase,
    private val resourcesRepository: ResourcesRepository,
    private val coroutineProvider: CoroutineProvider,
    private val application: Application,
) : ViewModel() {

    companion object {
        private val ALL_FILTER_TYPES = listOf(
            EstateType,
            Price,
            Surface,
            PhotoCount,
            PointOfInterest,
            SaleStatus,
        )
    }

    private val backStackEntryCountMutableStateFlow = MutableStateFlow(0)
    private val isFilteringMutableStateFlow = MutableStateFlow(false)

    val viewStateLiveData: LiveData<MainViewState> = combine(
        currentEstateRepository.getIdOrNull(),
        backStackEntryCountMutableStateFlow,
        isFilteringMutableStateFlow,
        resourcesRepository.isTabletFlow(),
        filterRepository.getAppliedFiltersFlow(),
    ) { currentEstateId, backStackEntryCount, isFiltering, isTablet, appliedFilters ->

        // Set when to open the estate details
        withContext(coroutineProvider.getMainDispatcher()) {
            if (currentEstateId != null && !isTablet && backStackEntryCount == 0) {
                mainSingleLiveEvent.value = MainEvent.OpenEstateDetail
            }
        }

        val isDetailInPortrait = !isTablet && backStackEntryCount == 1

        MainViewState(
            toolbarTitle = if (isDetailInPortrait) {
                application.getString(R.string.toolbar_title_detail)
            } else {
                application.getString(R.string.app_name)
            },
            navigationIconId = if (isDetailInPortrait) R.drawable.ic_arrow_back else null,
            isEditMenuItemVisible = currentEstateId != null,
            isFiltering = isFiltering && !isDetailInPortrait,
            chips = ALL_FILTER_TYPES.map { filterType ->
                val filterValue = appliedFilters[filterType]

                MainViewState.ChipViewState(
                    style = if (filterValue != null) {
                        MainViewState.ChipViewState.Style(
                            text = getSelectedChipLabel(filterValue),
                            backgroundColor = R.color.colorAccent,
                            isCloseIconVisible = true,
                        )
                    } else {
                        MainViewState.ChipViewState.Style(
                            text = getDefaultChipLabel(filterType),
                            backgroundColor = R.color.lightGray,
                            isCloseIconVisible = false,
                        )
                    },
                    onFilterClicked = {
                        mainSingleLiveEvent.value = when (filterType) {
                            is Slider -> MainEvent.ShowSliderFilterSettings(filterType, filterValue)
                            is CheckList -> MainEvent.ShowCheckListFilterSettings(filterType, filterValue)
                            is SaleStatus -> MainEvent.ShowDateFilterSettings(filterType, filterValue)
                        }
                    },
                    onCloseIconClicked = { filterRepository.clear(filterType) },
                )
            },
        )
    }.asLiveData(coroutineProvider.getIoDispatcher())

    private fun getDefaultChipLabel(filterType: FilterType): String {
        return application.getString(
            when (filterType) {
                EstateType -> R.string.hint_type
                PointOfInterest -> R.string.label_points_of_interest
                SaleStatus -> R.string.filter_sale_status
                PhotoCount -> R.string.filter_photo
                Price -> R.string.hint_price
                Surface -> R.string.hint_area
            }
        )
    }

    private fun getSelectedChipLabel(filterValue: FilterValue): String {
        return when (filterValue) {
            is FilterValue.MinMax -> getMinMaxFilterLabel(filterValue)
            is FilterValue.Choices -> getChoicesFilterLabel(filterValue)
            is FilterValue.Date -> getSaleStatusFilterLabel(filterValue)
        }
    }

    private fun getMinMaxFilterLabel(minMaxFilter: FilterValue.MinMax): String {
        return application.getString(
            when (minMaxFilter) {
                is FilterValue.PhotoCount -> R.string.filter_photo_count_range_short
                is FilterValue.Price -> R.string.filter_price_range_short
                is FilterValue.Surface -> R.string.filter_surface_range_short
            },
            minMaxFilter.min.toInt(),
            minMaxFilter.max.toInt(),
        )
    }

    private fun getChoicesFilterLabel(choicesFilter: FilterValue.Choices): String {
        val selectedItems = choicesFilter.selectedItems.map { it.stringId }

        val displayedItems = selectedItems
            .take(3) // Only display the first 3 items
            .map { stringRes -> application.getString(stringRes) }

        val itemCountOverflow = selectedItems.size - displayedItems.size

        val stringBuilder = StringBuilder(displayedItems.joinToString())

        if (itemCountOverflow > 0) {
            stringBuilder.append(" (+$itemCountOverflow)")
        }

        return stringBuilder.toString()
    }

    private fun getSaleStatusFilterLabel(dateFilter: FilterValue.Date): String {
        val min = dateFilter.from?.format(UtilsRepository.SHORT_DATE_FORMATTER)
        val max = dateFilter.until?.format(UtilsRepository.SHORT_DATE_FORMATTER)


        return if (dateFilter.availableEstates) {
            if (min != null) {
                if (max != null) {
                    application.getString(R.string.filter_available_between, min, max)
                } else {
                    application.getString(R.string.filter_available_from, min)
                }
            } else {
                if (max != null) {
                    application.getString(R.string.filter_available_until, max)
                } else {
                    application.getString(R.string.filter_available_all)
                }
            }
        } else {
            if (min != null) {
                if (max != null) {
                    application.getString(R.string.filter_sold_between, min, max)
                } else {
                    application.getString(R.string.filter_sold_from, min)
                }
            } else {
                if (max != null) {
                    application.getString(R.string.filter_sold_until, max)
                } else {
                    application.getString(R.string.filter_sold_all)
                }
            }
        }
    }

    private val mainSingleLiveEvent = SingleLiveEvent<MainEvent>()
    val mainEventLiveData: LiveData<MainEvent> = mainSingleLiveEvent

    fun onAddMenuItemClicked() {
        initAndOpenForm(FormType.ADD_ESTATE)
    }

    fun onEditMenuItemClicked() {
        initAndOpenForm(FormType.EDIT_ESTATE)
    }

    fun onFilterMenuItemClicked() {
        isFilteringMutableStateFlow.update { isVisible -> !isVisible }
    }

    private fun initAndOpenForm(formType: FormType) {
        viewModelScope.launch(coroutineProvider.getIoDispatcher()) {
            setFormUseCase.initForm(formType)

            withContext(coroutineProvider.getMainDispatcher()) {
                mainSingleLiveEvent.value = MainEvent.OpenEstateForm
            }
        }
    }

    fun onBackStackChanged(backStackEntryCount: Int) {
        // Reset current estate ID, if go back to list view from detail view in portrait orientation
        if (backStackEntryCountMutableStateFlow.value == 1 && backStackEntryCount == 0) {
            currentEstateRepository.setId(null)
        }
        backStackEntryCountMutableStateFlow.value = backStackEntryCount
    }

    fun onActivityResumed() {
        resourcesRepository.refreshOrientation()
    }
}