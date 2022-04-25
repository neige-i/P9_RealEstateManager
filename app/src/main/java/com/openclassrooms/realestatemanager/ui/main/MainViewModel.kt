package com.openclassrooms.realestatemanager.ui.main

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.ResourcesRepository
import com.openclassrooms.realestatemanager.data.real_estate.CurrentEstateRepository
import com.openclassrooms.realestatemanager.domain.form.FormType
import com.openclassrooms.realestatemanager.domain.form.SetFormUseCase
import com.openclassrooms.realestatemanager.ui.util.CoroutineProvider
import com.openclassrooms.realestatemanager.ui.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val currentEstateRepository: CurrentEstateRepository,
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

        // Set when to open the estate details
        withContext(coroutineProvider.getMainDispatcher()) {
            if (currentEstateId != null && !isTablet && backStackEntryCount == 0) {
                mainEventSingleLiveEvent.value = MainEvent.OpenEstateDetail
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
        )
    }.asLiveData(coroutineProvider.getIoDispatcher())

    private val mainEventSingleLiveEvent = SingleLiveEvent<MainEvent>()
    val mainEventLiveData: LiveData<MainEvent> = mainEventSingleLiveEvent

    fun onAddMenuItemClicked() {
        initAndOpenForm(FormType.ADD_ESTATE)
    }

    fun onEditMenuItemClicked() {
        initAndOpenForm(FormType.EDIT_ESTATE)
    }

    private fun initAndOpenForm(formType: FormType) {
        viewModelScope.launch(coroutineProvider.getIoDispatcher()) {
            setFormUseCase.initForm(formType)

            withContext(coroutineProvider.getMainDispatcher()) {
                mainEventSingleLiveEvent.value = MainEvent.OpenEstateForm
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