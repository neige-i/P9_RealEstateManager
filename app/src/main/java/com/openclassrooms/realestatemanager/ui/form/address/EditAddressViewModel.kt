package com.openclassrooms.realestatemanager.ui.form.address

import androidx.annotation.StringRes
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.data.PointOfInterest
import com.openclassrooms.realestatemanager.domain.form.SetFormUseCase
import com.openclassrooms.realestatemanager.domain.form.GetFormUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditAddressViewModel @Inject constructor(
    getFormUseCase: GetFormUseCase,
    private val setFormUseCase: SetFormUseCase,
) : ViewModel() {

    val viewStateLiveData = Transformations.map(getFormUseCase.getUpdates()) {
        AddressViewState(
            streetNumber = it.streetName,
            streetNumberError = it.streetNameError,
            additionalInfo = it.additionalAddressInfo,
            city = it.city,
            cityError = it.cityError,
            state = it.state,
            stateError = it.stateError,
            zipcode = it.zipcode,
            zipcodeError = it.zipcodeError,
            country = it.country,
            countryError = it.countryError,
            pointOfInterestList = PointOfInterest.values().map { poi ->
                AddressViewState.ChipViewState(
                    labelId = poi.labelId,
                    isSelected = it.pointsOfInterests.contains(poi.labelId)
                )
            }
        )
    }

    fun onStreetNameChanged(streetName: String?) {
        setFormUseCase.updateStreetName(streetName ?: "")
    }

    fun onAdditionalAddressInfoChanged(additionalAddressInfo: String?) {
        setFormUseCase.updateAdditionalAddressInfo(additionalAddressInfo ?: "")
    }

    fun onCityChanged(city: String?) {
        setFormUseCase.updateCity(city ?: "")
    }

    fun onStateNameChanged(state: String?) {
        setFormUseCase.updateState(state ?: "")
    }

    fun onZipcodeChanged(zipcode: String?) {
        setFormUseCase.updateZipcode(zipcode ?: "")
    }

    fun onCountryChanged(country: String?) {
        setFormUseCase.updateCountry(country ?: "")
    }

    fun onPoiChecked(@StringRes labelId: Int, isChecked: Boolean) {
        setFormUseCase.updatePoi(labelId, isChecked)
    }
}