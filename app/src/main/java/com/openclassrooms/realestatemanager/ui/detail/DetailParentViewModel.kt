package com.openclassrooms.realestatemanager.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.data.DetailRepository
import com.openclassrooms.realestatemanager.ui.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailParentViewModel @Inject constructor(private val detailRepository: DetailRepository) :
    ViewModel() {

    private val endActivityMutableEvent = SingleLiveEvent<Unit>()
    val endActivityEvent: LiveData<Unit> = endActivityMutableEvent

    fun onActivityResumed(isTablet: Boolean) {
        // Do not display DetailActivity in tablet configuration
        if (isTablet) {
            endActivityMutableEvent.call()
        }
    }

    fun onManuallyGoBack() = detailRepository.resetItem()
}