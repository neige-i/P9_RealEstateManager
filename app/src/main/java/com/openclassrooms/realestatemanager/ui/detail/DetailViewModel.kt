package com.openclassrooms.realestatemanager.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.openclassrooms.realestatemanager.domain.real_estate.GetCurrentEstateIdUseCase
import com.openclassrooms.realestatemanager.ui.util.CoroutineProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    getCurrentEstateIdUseCase: GetCurrentEstateIdUseCase,
    coroutineProvider: CoroutineProvider,
) : ViewModel() {

    private val viewStateMediatorLiveData = MediatorLiveData<String>()
    val viewState: LiveData<String> = viewStateMediatorLiveData

    init {
        viewStateMediatorLiveData.value = "No item selected"

        viewStateMediatorLiveData.addSource(
            getCurrentEstateIdUseCase.invoke().asLiveData(coroutineProvider.getIoDispatcher())
        ) {
            viewStateMediatorLiveData.value = it.toString()
        }
    }
}