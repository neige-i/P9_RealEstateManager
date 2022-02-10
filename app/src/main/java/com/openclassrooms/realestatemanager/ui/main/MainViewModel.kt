package com.openclassrooms.realestatemanager.ui.main

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.DetailRepository
import com.openclassrooms.realestatemanager.ui.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    detailRepository: DetailRepository,
    private val application: Application,
) : ViewModel() {

    private val mainEventSingleLiveEvent = SingleLiveEvent<MainEvent>()
    val mainEventLiveData: LiveData<MainEvent> = mainEventSingleLiveEvent

    init {
        mainEventSingleLiveEvent.addSource(detailRepository.getItemLiveData()) {
            if (!application.resources.getBoolean(R.bool.is_tablet)) {
                mainEventSingleLiveEvent.value = MainEvent.GoToDetailActivity
            }
        }
    }

    fun onMenuItemClicked(itemId: Int): Boolean {
        return when (itemId) {
            R.id.toolbar_menu_add -> {
                mainEventSingleLiveEvent.value = MainEvent.GoToFormActivity
                true
            }
            else -> false
        }
    }
}