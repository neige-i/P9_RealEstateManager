package com.openclassrooms.realestatemanager.ui.add_edit.pages

import androidx.annotation.StringRes
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.data.PointOfInterest
import com.openclassrooms.realestatemanager.domain.EditFormUseCase
import com.openclassrooms.realestatemanager.domain.GetFormInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(
    getFormInfoUseCase: GetFormInfoUseCase,
    private val editFormUseCase: EditFormUseCase,
) : ViewModel() {

    val viewStateLiveData = Transformations.map(getFormInfoUseCase()) {
        AddressViewState(
            streetNumber = it.streetNameHouseNumber,
            additionalInfo = it.additionalAddressInfo,
            city = it.city,
            state = it.state,
            zipcode = it.zipcode,
            country = it.country,
            pointOfInterestList = PointOfInterest.values().map { poi ->
                AddressViewState.ChipViewState(
                    labelId = poi.labelId,
                    isSelected = it.pointsOfInterests.contains(poi.labelId)
                )
            }
        )
    }

    fun onStreetNameChanged(streetName: String?) {
        editFormUseCase.updateStreetNameHouseNumber(streetName ?: "")
    }

    fun onAdditionalAddressInfoChanged(additionalAddressInfo: String?) {
        editFormUseCase.updateAdditionalAddressInfo(additionalAddressInfo ?: "")
    }

    fun onCityChanged(city: String?) {
        editFormUseCase.updateCity(city ?: "")
    }

    fun onStateNameChanged(state: String?) {
        editFormUseCase.updateState(state ?: "")
    }

    fun onZipcodeChanged(zipcode: String?) {
        editFormUseCase.updateZipcode(zipcode ?: "")
    }

    fun onCountryChanged(country: String?) {
        editFormUseCase.updateCountry(country ?: "")
    }

    fun onPoiChecked(@StringRes labelId: Int, isChecked: Boolean) {
        editFormUseCase.updatePoi(labelId, isChecked)
    }
}