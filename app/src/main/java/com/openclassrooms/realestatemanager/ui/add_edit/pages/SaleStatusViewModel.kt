package com.openclassrooms.realestatemanager.ui.add_edit.pages

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SaleStatusViewModel @Inject constructor() : ViewModel() {

    private val mutableViewState = MutableLiveData(true)
    val viewState: LiveData<Boolean> = mutableViewState

    fun onAvailableSwitched(isAvailable: Boolean) {
        mutableViewState.value = isAvailable
    }
}