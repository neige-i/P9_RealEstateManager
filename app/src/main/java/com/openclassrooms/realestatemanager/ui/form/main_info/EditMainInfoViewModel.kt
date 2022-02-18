package com.openclassrooms.realestatemanager.ui.form.main_info

import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.domain.form.GetFormUseCase
import com.openclassrooms.realestatemanager.domain.form.SetFormUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditMainInfoViewModel @Inject constructor(
    getFormUseCase: GetFormUseCase,
    private val setFormUseCase: SetFormUseCase,
) : ViewModel() {

    val viewStateLiveData = Transformations.map(getFormUseCase.getUpdates()) {
        MainInfoViewState(
            selectedType = it.type,
            typeError = it.typeError,
            price = it.price,
            priceSelection = it.priceCursor,
            area = it.area,
            areaSelection = it.areaCursor,
            totalRoomCount = it.totalRoomCount.toString(),
            bathroomCount = it.bathroomCount.toString(),
            bedroomCount = it.bedroomCount.toString()
        )
    }

    fun onTypeSelected(selectedType: String) {
        setFormUseCase.updateType(selectedType)
    }

    fun onPriceChanged(price: String?, cursorPosition: Int) {
        setFormUseCase.updatePrice(price ?: "", cursorPosition)
    }

    fun onAreaChanged(area: String?, cursorPosition: Int) {
        setFormUseCase.updateArea(area ?: "", cursorPosition)
    }

    fun onTotalRoomAdded() {
        setFormUseCase.incTotalRoomCount()
    }

    fun onTotalRoomRemoved() {
        setFormUseCase.decTotalRoomCount()
    }

    fun onBathRoomAdded() {
        setFormUseCase.incBathroomCount()
    }

    fun onBathRoomRemoved() {
        setFormUseCase.decBathroomCount()
    }

    fun onBedRoomAdded() {
        setFormUseCase.incBedroomCount()
    }

    fun onBedRoomRemoved() {
        setFormUseCase.decBedroomCount()
    }
}