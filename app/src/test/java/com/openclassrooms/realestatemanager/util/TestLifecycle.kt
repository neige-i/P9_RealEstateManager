package com.openclassrooms.realestatemanager.util

import androidx.lifecycle.LiveData

object TestLifecycle {
    fun <T> getValueForTesting(liveData: LiveData<T>): T? {
        liveData.observeForever { }
        return liveData.value
    }

    fun <T> isLiveDataTriggered(liveData: LiveData<T>): Boolean {
        var isCalled = false
        liveData.observeForever {
            isCalled = true
        }
        return isCalled
    }
}