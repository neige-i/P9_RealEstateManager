package com.openclassrooms.realestatemanager.ui.add_edit.pages

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainInfoViewModel @Inject constructor() : ViewModel() {

    private val viewStateMutableLiveData = MutableLiveData<MainInfoViewState>()
    val viewStateLiveData: LiveData<MainInfoViewState> = viewStateMutableLiveData

    private var currentState = MainInfoViewState(
        totalRoomCount = 0,
        bathroomCount = 0,
        bedroomCount = 0
    )

    init {
        viewStateMutableLiveData.value = currentState
    }

    fun onTotalRoomAdded() {
        updateRoom(
            newState = currentState.copy(totalRoomCount = currentState.totalRoomCount.inc()),
            conditionToUpdate = true
        )
    }

    fun onBathRoomAdded() {
        updateRoom(
            newState = currentState.copy(bathroomCount = currentState.bathroomCount.inc()),
            conditionToUpdate = areAnyRoomsLeft()
        )
    }

    fun onBedRoomAdded() {
        updateRoom(
            newState = currentState.copy(bedroomCount = currentState.bedroomCount.inc()),
            conditionToUpdate = areAnyRoomsLeft()
        )
    }

    fun onTotalRoomRemoved() {
        updateRoom(
            newState = currentState.copy(totalRoomCount = currentState.totalRoomCount.dec()),
            conditionToUpdate = areAnyRoomsLeft()
        )
    }

    fun onBathRoomRemoved() {
        val currentBathRoomCount = currentState.bathroomCount
        updateRoom(
            newState = currentState.copy(bathroomCount = currentBathRoomCount.dec()),
            conditionToUpdate = currentBathRoomCount > 0
        )
    }

    fun onBedRoomRemoved() {
        val currentBedRoomCount = currentState.bedroomCount
        updateRoom(
            newState = currentState.copy(bedroomCount = currentBedRoomCount.dec()),
            conditionToUpdate = currentBedRoomCount > 0
        )
    }

    private fun updateRoom(newState: MainInfoViewState, conditionToUpdate: Boolean) {
        if (conditionToUpdate) {
            currentState = newState
            viewStateMutableLiveData.value = currentState
        }
    }

    private fun areAnyRoomsLeft(): Boolean = currentState.run {
        totalRoomCount - bathroomCount - bedroomCount > 0
    }
}