package com.openclassrooms.realestatemanager.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DetailRepository @Inject constructor() {

    private val itemMutableLiveData = MutableLiveData<String?>(null)

    fun getItemLiveData(): LiveData<String?> = itemMutableLiveData

    fun setItem(item: String) {
        itemMutableLiveData.value = item
    }

    fun resetItem() {
        itemMutableLiveData.value = null
    }
}