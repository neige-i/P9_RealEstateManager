package com.openclassrooms.realestatemanager.ui.form.address

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.openclassrooms.realestatemanager.data.PointOfInterest
import com.openclassrooms.realestatemanager.domain.form.GetFormUseCase
import com.openclassrooms.realestatemanager.domain.form.SetFormUseCase
import com.openclassrooms.realestatemanager.ui.util.CoroutineProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class EditAddressViewModel @Inject constructor(
    getFormUseCase: GetFormUseCase,
    private val setFormUseCase: SetFormUseCase,
    coroutineProvider: CoroutineProvider,
) : ViewModel() {

    val viewStateLiveData: LiveData<AddressViewState> = getFormUseCase.getFormFlow().map { form ->
        AddressViewState(
            streetNumber = form.streetName,
            streetNumberError = form.streetNameError,
            streetNumberSelection = form.streetNameCursor,
            additionalInfo = form.additionalAddressInfo,
            additionalInfoSelection = form.additionalAddressInfoCursor,
            city = form.city,
            cityError = form.cityError,
            citySelection = form.cityCursor,
            state = form.state,
            stateError = form.stateError,
            stateSelection = form.stateCursor,
            zipcode = form.zipcode,
            zipcodeError = form.zipcodeError,
            zipcodeSelection = form.zipcodeCursor,
            country = form.country,
            countryError = form.countryError,
            countrySelection = form.countryCursor,
            pointOfInterestList = PointOfInterest.values().map { poi ->
                AddressViewState.ChipViewState(
                    labelId = poi.labelId,
                    isSelected = form.pointsOfInterests.contains(poi)
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
        val checkedPoi = PointOfInterest.values().first { poi ->
            poi.labelId == labelId
        }
        setFormUseCase.updatePoi(checkedPoi, isChecked)
    }
}