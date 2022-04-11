package com.openclassrooms.realestatemanager.ui.detail

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.PointOfInterest
import com.openclassrooms.realestatemanager.data.real_estate.RealEstateEntity
import com.openclassrooms.realestatemanager.domain.real_estate.GetCurrentEstateUseCase
import com.openclassrooms.realestatemanager.domain.real_estate.RealEstateResult
import com.openclassrooms.realestatemanager.ui.util.CoroutineProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    getCurrentEstateUseCase: GetCurrentEstateUseCase,
    coroutineProvider: CoroutineProvider,
    application: Application,
) : ViewModel() {

    val viewState: LiveData<DetailViewState> = getCurrentEstateUseCase.invoke().map {
        when (it) {
            is RealEstateResult.Success -> getInfoViewState(
                realEstate = it.realEstate,
                application = application
            )
            is RealEstateResult.Failure -> DetailViewState.Empty(
                noSelectionLabelText = application.getString(
                    R.string.unknown_real_estate_selected,
                    it.estateId
                )
            )
            RealEstateResult.Idle -> DetailViewState.Empty(
                noSelectionLabelText = application.getString(R.string.no_real_estate_selected)
            )
        }
    }.asLiveData(coroutineProvider.getIoDispatcher())

    private fun getInfoViewState(
        realEstate: RealEstateEntity,
        application: Application
    ): DetailViewState.Info {
        val availableForSale = realEstate.info.saleDate == null

        val additionalAddressText = if (realEstate.info.additionalAddressInfo.isNotEmpty()) {
            "${realEstate.info.additionalAddressInfo}\n"
        } else {
            ""
        }

        return DetailViewState.Info(
            saleText = application.getString(
                if (availableForSale) R.string.for_sale else R.string.sold
            ),
            saleBackgroundColor = if (availableForSale) {
                android.R.color.holo_green_dark
            } else {
                android.R.color.holo_red_dark
            },
            photoList = realEstate.photoList
                .map {
                    DetailViewState.Info.Photo(
                        url = it.uri,
                        description = it.description
                    )
                },
            description = realEstate.info.description
                .ifEmpty {
                    application.getString(R.string.not_available)
                },
            surface = realEstate.info.area?.toString()
                ?: application.getString(R.string.not_available),
            roomCount = realEstate.info.totalRoomCount.toString(),
            bathroomCount = realEstate.info.bathroomCount.toString(),
            bedroomCount = realEstate.info.bedroomCount.toString(),
            address = "${realEstate.info.streetName}\n" +
                    additionalAddressText +
                    "${realEstate.info.city}\n" +
                    "${realEstate.info.state} ${realEstate.info.zipcode}\n" +
                    realEstate.info.country,
            poiList = realEstate.poiList.map { PointOfInterest.valueOf(it.poiValue).labelId },
            market_dates = if (availableForSale) {
                application.getString(R.string.since_date, realEstate.info.marketEntryDate)
            } else {
                application.getString(
                    R.string.from_sold_date,
                    realEstate.info.marketEntryDate,
                    realEstate.info.saleDate
                )
            },
            agentName = realEstate.agent?.username ?: application.getString(R.string.not_available)
        )
    }

    fun onMediaClicked(photo: DetailViewState.Info.Photo) {
        Log.d("Neige", "onMediaClicked() called with: photo = $photo")
    }
}