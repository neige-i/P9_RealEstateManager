package com.openclassrooms.realestatemanager.ui.form.address

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.openclassrooms.realestatemanager.data.PointOfInterest
import com.openclassrooms.realestatemanager.domain.form.GetFormUseCase
import com.openclassrooms.realestatemanager.domain.form.SetFormUseCase
import com.openclassrooms.realestatemanager.ui.CoroutineProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class EditAddressViewModel @Inject constructor(
    getFormUseCase: GetFormUseCase,
    private val setFormUseCase: SetFormUseCase,
    coroutineProvider: CoroutineProvider,
) : ViewModel() {

    val viewStateLiveData: LiveData<AddressViewState> = getFormUseCase.getFormFlow().map {
        AddressViewState(
            streetNumber = it.streetName,
            streetNumberError = it.streetNameError,
            streetNumberSelection = it.streetNameCursor,
            additionalInfo = it.additionalAddressInfo,
            additionalInfoSelection = it.additionalAddressInfoCursor,
            city = it.city,
            cityError = it.cityError,
            citySelection = it.cityCursor,
            state = it.state,
            stateError = it.stateError,
            stateSelection = it.stateCursor,
            zipcode = it.zipcode,
            zipcodeError = it.zipcodeError,
            zipcodeSelection = it.zipcodeCursor,
            country = it.country,
            countryError = it.countryError,
            countrySelection = it.countryCursor,
            pointOfInterestList = PointOfInterest.values().map { poi ->
                AddressViewState.ChipViewState(
                    labelId = poi.labelId,
                    isSelected = it.pointsOfInterests.contains(poi.labelId)
                )
            }
        )
    }.asLiveData(coroutineProvider.getIoDispatcher())

    fun onStreetNameChanged(streetName: String?, cursorPosition: Int) {
        setFormUseCase.updateStreetName(streetName ?: "", cursorPosition)
    }

    fun onAdditionalAddressInfoChanged(additionalAddressInfo: String?, cursorPosition: Int) {
        setFormUseCase.updateAdditionalAddressInfo(additionalAddressInfo ?: "", cursorPosition)
    }

    fun onCityChanged(city: String?, cursorPosition: Int) {
        setFormUseCase.updateCity(city ?: "", cursorPosition)
    }

    fun onStateNameChanged(state: String?, cursorPosition: Int) {
        setFormUseCase.updateState(state ?: "", cursorPosition)
    }

    fun onZipcodeChanged(zipcode: String?, cursorPosition: Int) {
        setFormUseCase.updateZipcode(zipcode ?: "", cursorPosition)
    }

    fun onCountryChanged(country: String?, cursorPosition: Int) {
        setFormUseCase.updateCountry(country ?: "", cursorPosition)
    }

    fun onPoiChecked(@StringRes labelId: Int, isChecked: Boolean) {
        setFormUseCase.updatePoi(labelId, isChecked)
    }
}