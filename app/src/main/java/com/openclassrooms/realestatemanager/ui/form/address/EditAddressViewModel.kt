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
            additionalInfo = form.additionalAddressInfo,
            city = form.city,
            cityError = form.cityError,
            state = form.state,
            stateError = form.stateError,
            zipcode = form.zipcode,
            zipcodeError = form.zipcodeError,
            country = form.country,
            countryError = form.countryError,
            pointOfInterestList = PointOfInterest.values().map { poi ->
                AddressViewState.ChipViewState(
                    labelId = poi.labelId,
                    isSelected = form.pointsOfInterests.contains(poi)
                )
            }
        )
    }.asLiveData(coroutineProvider.getIoDispatcher())

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
        val checkedPoi = PointOfInterest.values().first { poi ->
            poi.labelId == labelId
        }
        setFormUseCase.updatePoi(checkedPoi, isChecked)
    }
}