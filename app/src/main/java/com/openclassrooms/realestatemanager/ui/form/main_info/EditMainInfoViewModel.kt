package com.openclassrooms.realestatemanager.ui.form.main_info

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.openclassrooms.realestatemanager.data.RealEstateType
import com.openclassrooms.realestatemanager.domain.form.GetFormUseCase
import com.openclassrooms.realestatemanager.domain.form.SetFormUseCase
import com.openclassrooms.realestatemanager.ui.util.CoroutineProvider
import com.openclassrooms.realestatemanager.ui.util.LocalText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class EditMainInfoViewModel @Inject constructor(
    getFormUseCase: GetFormUseCase,
    private val setFormUseCase: SetFormUseCase,
    coroutineProvider: CoroutineProvider,
    private val application: Application,
) : ViewModel() {

    val viewStateLiveData = getFormUseCase.getFormFlow().map { form ->
        MainInfoViewState(
            selectedType = if (form.estateType != null) {
                LocalText.Res(stringId = form.estateType.labelId)
            } else {
                LocalText.Simple(content = "")
            },
            typeError = form.typeError,
            price = form.price,
            area = form.area,
            totalRoomCount = form.totalRoomCount.toString(),
            bathroomCount = form.bathroomCount.toString(),
            bedroomCount = form.bedroomCount.toString()
        )
    }.asLiveData(coroutineProvider.getIoDispatcher())

    fun onTypeSelected(selectedType: String) {
        setFormUseCase.updateType(
            RealEstateType.values().first { estateType ->
                application.getString(estateType.labelId) == selectedType
            }
        )
    }

    fun onPriceChanged(price: String?) {
        setFormUseCase.updatePrice(price ?: "")
    }

    fun onAreaChanged(area: String?) {
        setFormUseCase.updateArea(area ?: "")
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