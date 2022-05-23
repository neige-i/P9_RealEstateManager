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
import com.openclassrooms.realestatemanager.domain.filter.SetFilterUseCase
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
    private val filterRepository: FilterRepository,
    setFilterUseCase: SetFilterUseCase,
    private val currentEstateRepository: CurrentEstateRepository,
    private val setFormUseCase: SetFormUseCase,
    private val resourcesRepository: ResourcesRepository,
    private val coroutineProvider: CoroutineProvider,
    private val application: Application,
) : ViewModel() {

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
            filterList = appliedFilters.map { (filterType, filterValue) ->
                MainViewState.FilterViewState(
                    text = if (filterValue == null) {
                        application.getString(filterType.labelId)
                    } else {
                        when (filterValue) {
                            is FilterValue.IntMinMax -> getFilterLabel(
                                filterType = filterType,
                                from = filterValue.value.lower,
                                to = filterValue.value.upper,
                            )
                            is FilterValue.DoubleMinMax -> getFilterLabel(
                                filterType = filterType,
                                from = filterValue.value.lower,
                                to = filterValue.value.upper,
                            )
                            is FilterValue.DateMinMax -> getFilterLabel(filterValue)
                            is FilterValue.TypeChoice -> getFilterLabel(
                                filterValue.value.map { application.getString(it.labelId) }
                            )
                            is FilterValue.PoiChoice -> getFilterLabel(
                                filterValue.value.map { application.getString(it.labelId) }
                            )
                        }
                    },
                    backgroundColor = if (filterValue != null) {
                        R.color.colorAccent
                    } else {
                        R.color.lightGray
                    },
                    isCloseIconVisible = filterValue != null,
                    onFilterClicked = {
                        filterRepository.setCurrentFilter(filterType)
                        mainSingleLiveEvent.value = when (filterType) {
                            PRICE, SURFACE, PHOTO_COUNT -> {
                                MainEvent.ShowSliderFilterDialog
                            }
                            TYPE, POINT_OF_INTEREST -> {
                                MainEvent.ShowCheckableFilterDialog
                            }
                            SALE_STATUS -> {
                                MainEvent.ShowCalendarFilterDialog
                            }
                        }
                    },
                    onCloseIconClicked = { setFilterUseCase.reset(filterType) },
                )
            },
        )
    }.asLiveData(coroutineProvider.getIoDispatcher())

    private fun getFilterLabel(filterType: FilterType, from: Number, to: Number): String {
        return application.getString(
            when (filterType) {
                TYPE -> TODO()
                PRICE -> R.string.filter_price_range_short
                SURFACE -> R.string.filter_surface_range_short
                PHOTO_COUNT -> R.string.filter_photo_count_range_short
                POINT_OF_INTEREST -> TODO()
                SALE_STATUS -> TODO()
            },
            from.toInt(),
            to.toInt(),
        )
    }

    private fun getFilterLabel(selectedItems: List<String>): String {
        val MAX_ITEM_COUNT = 3

        val stringBuilder = StringBuilder(selectedItems.take(MAX_ITEM_COUNT).joinToString())

        val remainItems = selectedItems.size - MAX_ITEM_COUNT
        if (remainItems > 0) {
            stringBuilder.append(" (+$remainItems)")
        }

        return stringBuilder.toString()
    }

    private fun getFilterLabel(dateFilterValue: FilterValue.DateMinMax): String {
        val min = dateFilterValue.min?.format(UtilsRepository.SHORT_DATE_FORMATTER)
        val max = dateFilterValue.max?.format(UtilsRepository.SHORT_DATE_FORMATTER)


        return if (dateFilterValue.availableEstates) {
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