package com.openclassrooms.realestatemanager.ui.detail

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.openclassrooms.realestatemanager.BuildConfig
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.PointOfInterest
import com.openclassrooms.realestatemanager.data.ResourcesRepository
import com.openclassrooms.realestatemanager.data.real_estate.RealEstateEntity
import com.openclassrooms.realestatemanager.domain.real_estate.GetCurrentEstateUseCase
import com.openclassrooms.realestatemanager.domain.real_estate.RealEstateResult.*
import com.openclassrooms.realestatemanager.ui.util.CoroutineProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import java.net.URLEncoder
import java.text.NumberFormat
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    getCurrentEstateUseCase: GetCurrentEstateUseCase,
    resourcesRepository: ResourcesRepository,
    coroutineProvider: CoroutineProvider,
    application: Application,
    private val numberFormat: NumberFormat
) : ViewModel() {

    val viewStateLiveData: LiveData<DetailViewState> = combine(
        getCurrentEstateUseCase.invoke(),
        resourcesRepository.isTabletFlow(),
    ) { realEstateResult, isTablet ->
        when (realEstateResult) {
            is Success -> getInfoViewState(realEstateResult.realEstate, isTablet, application)
            is Failure -> DetailViewState.Empty(
                noSelectionLabelText = application.getString(
                    R.string.unknown_real_estate_selected,
                    realEstateResult.estateId
                )
            )
            Idle -> DetailViewState.Empty(
                noSelectionLabelText = application.getString(R.string.no_real_estate_selected)
            )
        }
    }.asLiveData(coroutineProvider.getIoDispatcher())

    private fun getInfoViewState(
        realEstate: RealEstateEntity,
        isTablet: Boolean,
        application: Application
    ): DetailViewState.WithInfo {
        val availableForSale = realEstate.info.saleDate == null

        val additionalAddressText = if (realEstate.info.additionalAddressInfo.isNotEmpty()) {
            "${realEstate.info.additionalAddressInfo}\n"
        } else {
            ""
        }
        val urlEncodedAddress = URLEncoder.encode(
            "${realEstate.info.streetName}, ${realEstate.info.city}, ${realEstate.info.country}",
            "UTF-8"
        )

        return DetailViewState.WithInfo(
            type = realEstate.info.type,
            price = realEstate.info.price?.let { price ->
                application.getString(R.string.price_in_dollars, numberFormat.format(price))
            } ?: application.getString(R.string.undefined_price),
            areTypeAndPriceVisible = !isTablet,
            saleText = application.getString(
                if (availableForSale) R.string.for_sale else R.string.sold
            ),
            saleBackgroundColor = if (availableForSale) {
                android.R.color.holo_green_dark
            } else {
                android.R.color.holo_red_dark
            },
            photoList = realEstate.photoList.map {
                DetailViewState.WithInfo.Photo(
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
            mapUrl = "https://maps.googleapis.com/maps/api/staticmap?" +
                    "&size=400x400" +
                    "&markers=$urlEncodedAddress" +
                    "&key=${BuildConfig.STATIC_MAPS_API_KEY}",
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

    fun onPhotoClicked(photo: DetailViewState.WithInfo.Photo) {
        Log.d("Neige", "onMediaClicked() called with: photo = $photo")
    }
}