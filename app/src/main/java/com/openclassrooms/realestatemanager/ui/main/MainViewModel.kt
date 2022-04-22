package com.openclassrooms.realestatemanager.ui.main

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.ResourcesRepository
import com.openclassrooms.realestatemanager.data.real_estate.CurrentEstateRepository
import com.openclassrooms.realestatemanager.domain.form.SetFormUseCase
import com.openclassrooms.realestatemanager.domain.real_estate.GetCurrentEstateUseCase
import com.openclassrooms.realestatemanager.domain.real_estate.RealEstateResult
import com.openclassrooms.realestatemanager.ui.util.CoroutineProvider
import com.openclassrooms.realestatemanager.ui.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    currentEstateRepository: CurrentEstateRepository,
    private val getCurrentEstateUseCase: GetCurrentEstateUseCase,
    private val setFormUseCase: SetFormUseCase,
    private val resourcesRepository: ResourcesRepository,
    private val coroutineProvider: CoroutineProvider,
    private val application: Application,
) : ViewModel() {

    private val backStackEntryCountMutableStateFlow = MutableStateFlow(0)

    val viewStateLiveData: LiveData<MainViewState> = combine(
        currentEstateRepository.getIdOrNull(),
        backStackEntryCountMutableStateFlow,
        resourcesRepository.isTabletFlow(),
    ) { currentEstateId, backStackEntryCount, isTablet ->
        val isDetailInPortrait = !isTablet && backStackEntryCount == 1

        MainViewState(
            toolbarTitle = if (isDetailInPortrait) {
                application.getString(R.string.toolbar_title_detail)
            } else {
                application.getString(R.string.app_name)
            },
            navigationIconId = if (isDetailInPortrait) R.drawable.ic_arrow_back else null,
            isEditMenuItemVisible = isTablet && currentEstateId != null || isDetailInPortrait,
        )
    }.asLiveData(coroutineProvider.getIoDispatcher())

    private val mainEventSingleLiveEvent = SingleLiveEvent<MainEvent>()
    val mainEventLiveData: LiveData<MainEvent> = mainEventSingleLiveEvent

    init {
        viewModelScope.launch(coroutineProvider.getIoDispatcher()) {
            currentEstateRepository.getId().collect {
                val isTablet = resourcesRepository.isTabletFlow().first()

                withContext(coroutineProvider.getMainDispatcher()) {
                    if (!isTablet) {
                        mainEventSingleLiveEvent.value = MainEvent.OpenEstateDetail
                    }
                }
            }
        }
    }

    fun onAddMenuItemClicked() {
        setFormUseCase.initForm()
        mainEventSingleLiveEvent.value = MainEvent.OpenEstateForm
    }

    fun onEditMenuItemClicked() {
        viewModelScope.launch(coroutineProvider.getIoDispatcher()) {
            val currentEstateResult = getCurrentEstateUseCase().first()

            if (currentEstateResult is RealEstateResult.Success) {
                setFormUseCase.initForm(currentEstateResult.realEstate)

                withContext(coroutineProvider.getMainDispatcher()) {
                    mainEventSingleLiveEvent.value = MainEvent.OpenEstateForm
                }
            }
        }
    }

    fun onBackStackChanged(backStackEntryCount: Int) {
        backStackEntryCountMutableStateFlow.value = backStackEntryCount
    }

    fun onActivityResumed() {
        resourcesRepository.refreshOrientation()
    }
}