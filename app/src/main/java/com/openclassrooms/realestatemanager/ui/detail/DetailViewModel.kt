package com.openclassrooms.realestatemanager.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.openclassrooms.realestatemanager.data.real_estate.CurrentEstateRepository
import com.openclassrooms.realestatemanager.ui.util.CoroutineProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    currentEstateRepository: CurrentEstateRepository,
    coroutineProvider: CoroutineProvider,
) : ViewModel() {

    private val viewStateMediatorLiveData = MediatorLiveData<String>()
    val viewState: LiveData<String> = viewStateMediatorLiveData

    init {
        viewStateMediatorLiveData.value = "No item selected"

        viewStateMediatorLiveData.addSource(
            currentEstateRepository.getCurrentEstateId()
                .asLiveData(coroutineProvider.getIoDispatcher())
        ) {
            viewStateMediatorLiveData.value = it.toString()
        }
    }
}