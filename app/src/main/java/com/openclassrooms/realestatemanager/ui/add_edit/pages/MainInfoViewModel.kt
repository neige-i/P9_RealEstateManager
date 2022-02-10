package com.openclassrooms.realestatemanager.ui.add_edit.pages

import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.domain.EditFormUseCase
import com.openclassrooms.realestatemanager.domain.GetFormInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainInfoViewModel @Inject constructor(
    getFormInfoUseCase: GetFormInfoUseCase,
    private val editFormUseCase: EditFormUseCase,
) : ViewModel() {

    val viewStateLiveData = Transformations.map(getFormInfoUseCase()) {
        MainInfoViewState(
            selectedType = it.type,
            price = it.price,
            area = it.area,
            totalRoomCount = it.totalRoomCount.toString(),
            bathroomCount = it.bathroomCount.toString(),
            bedroomCount = it.bedroomCount.toString()
        )
    }

    fun onTypeSelected(selectedType: String) {
        editFormUseCase.updateType(selectedType)
    }

    fun onPriceChanged(price: String?) {
        editFormUseCase.updatePrice(price ?: "")
    }

    fun onAreaChanged(area: String?) {
        editFormUseCase.updateArea(area ?: "")
    }

    fun onTotalRoomAdded() {
        editFormUseCase.incTotalRoomCount()
    }

    fun onTotalRoomRemoved() {
        editFormUseCase.decTotalRoomCount()
    }

    fun onBathRoomAdded() {
        editFormUseCase.incBathroomCount()
    }

    fun onBathRoomRemoved() {
        editFormUseCase.decBathroomCount()
    }

    fun onBedRoomAdded() {
        editFormUseCase.incBedroomCount()
    }

    fun onBedRoomRemoved() {
        editFormUseCase.decBedroomCount()
    }
}