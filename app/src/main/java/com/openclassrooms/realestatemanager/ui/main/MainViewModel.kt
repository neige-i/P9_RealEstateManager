package com.openclassrooms.realestatemanager.ui.main

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    private val startDetailActivityMutableEvent = SingleLiveEvent<Unit>()
    val startDetailActivityEvent: LiveData<Unit> = startDetailActivityMutableEvent

    private val activityResumedPingMutableLiveData = MutableLiveData<Boolean>()

    init {
        val shownItemLiveData = detailRepository.getItemLiveData()

        startDetailActivityMutableEvent.addSource(shownItemLiveData) {
            setStartDetailActivity(it, activityResumedPingMutableLiveData.value)
        }
        startDetailActivityMutableEvent.addSource(activityResumedPingMutableLiveData) {
            setStartDetailActivity(shownItemLiveData.value, it)
        }
    }

    private fun setStartDetailActivity(item: String?, ping: Boolean?) {
        if (ping == true) {
            activityResumedPingMutableLiveData.value = false
            return
        }

        if (!application.resources.getBoolean(R.bool.is_tablet) && item != null) {
            startDetailActivityMutableEvent.call()
        }
    }

    fun onActivityResumed() {
        activityResumedPingMutableLiveData.value = true
    }
}