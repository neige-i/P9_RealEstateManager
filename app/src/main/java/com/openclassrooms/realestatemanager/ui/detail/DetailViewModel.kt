package com.openclassrooms.realestatemanager.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.data.DetailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(detailRepository: DetailRepository) : ViewModel() {

    private val viewStateMediatorLiveData = MediatorLiveData<String>()
    val viewState: LiveData<String> = viewStateMediatorLiveData

    init {
        viewStateMediatorLiveData.value = "No item selected"

        viewStateMediatorLiveData.addSource(detailRepository.getItemLiveData()) {
            viewStateMediatorLiveData.value = it
        }
    }
}