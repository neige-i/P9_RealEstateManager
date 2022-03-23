package com.openclassrooms.realestatemanager.ui.main

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.real_estate.CurrentEstateRepository
import com.openclassrooms.realestatemanager.domain.form.SetFormUseCase
import com.openclassrooms.realestatemanager.ui.util.CoroutineProvider
import com.openclassrooms.realestatemanager.ui.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    currentEstateRepository: CurrentEstateRepository,
    private val editFormUseCase: SetFormUseCase,
    coroutineProvider: CoroutineProvider,
    private val application: Application,
) : ViewModel() {

    private val mainEventSingleLiveEvent = SingleLiveEvent<MainEvent>()
    val mainEventLiveData: LiveData<MainEvent> = mainEventSingleLiveEvent

    init {
        mainEventSingleLiveEvent.addSource(
            currentEstateRepository.getId().asLiveData(coroutineProvider.getIoDispatcher())
        ) {
            if (!application.resources.getBoolean(R.bool.is_tablet)) {
                mainEventSingleLiveEvent.value = MainEvent.GoToDetailActivity
            }
        }
    }

    fun onAddMenuItemClicked() {
        editFormUseCase.initAddForm()
        mainEventSingleLiveEvent.value = MainEvent.GoToFormActivity
    }
}