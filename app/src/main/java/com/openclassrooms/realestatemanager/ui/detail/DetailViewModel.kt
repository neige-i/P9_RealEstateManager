package com.openclassrooms.realestatemanager.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.openclassrooms.realestatemanager.BuildConfig
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.ResourcesRepository
import com.openclassrooms.realestatemanager.data.UtilsRepository
import com.openclassrooms.realestatemanager.data.real_estate.RealEstateEntity
import com.openclassrooms.realestatemanager.domain.real_estate.GetCurrentEstateUseCase
import com.openclassrooms.realestatemanager.domain.real_estate.RealEstateResult.*
import com.openclassrooms.realestatemanager.ui.util.CoroutineProvider
import com.openclassrooms.realestatemanager.ui.util.LocalText
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
    private val numberFormat: NumberFormat
) : ViewModel() {

    val viewStateLiveData: LiveData<DetailViewState> = combine(
        getCurrentEstateUseCase.invoke(),
        resourcesRepository.isTabletFlow(),
    ) { realEstateResult, isTablet ->
        when (realEstateResult) {
            is Success -> getInfoViewState(realEstateResult.realEstate, isTablet)
            is Failure -> DetailViewState.Empty(
                noSelectionLabelText = LocalText.ResWithArgs(
                    stringId = R.string.unknown_real_estate_selected,
                    args = listOf(realEstateResult.estateId),
                )
            )
            is Idle -> DetailViewState.Empty(
                noSelectionLabelText = LocalText.Res(stringId = R.string.no_real_estate_selected)
            )
        }
    }.asLiveData(coroutineProvider.getIoDispatcher())

    private fun getInfoViewState(realEstate: RealEstateEntity, isTablet: Boolean): DetailViewState.Info {
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

        return DetailViewState.Info(
            type = realEstate.info.type.labelId,
            price = if (realEstate.info.price != null) {
                LocalText.ResWithArgs(
                    stringId = R.string.price_in_dollars,
                    args = listOf(numberFormat.format(realEstate.info.price)),
                )
            } else {
                LocalText.Res(stringId = R.string.undefined_price)
            },
            areTypeAndPriceVisible = !isTablet,
            saleText = if (availableForSale) R.string.for_sale else R.string.sold,
            saleBackgroundColor = if (availableForSale) {
                android.R.color.holo_green_dark
            } else {
                android.R.color.holo_red_dark
            },
            photoList = realEstate.photoList.map {
                PhotoViewState(
                    url = it.uri,
                    description = it.description,
                )
            },
            description = realEstate.info.description.let { estateDescription ->
                if (estateDescription.isNotEmpty()) {
                    LocalText.Simple(content = estateDescription)
                } else {
                    LocalText.Res(stringId = R.string.not_available)
                }
            },
            surface = if (realEstate.info.area != null) {
                LocalText.ResWithArgs(stringId = R.string.square_meters, args = listOf(realEstate.info.area))
            } else {
                LocalText.Res(stringId = R.string.not_available)
            },
            roomCount = realEstate.info.totalRoomCount.toString(),
            bathroomCount = realEstate.info.bathroomCount.toString(),
            bedroomCount = realEstate.info.bedroomCount.toString(),
            address = "${realEstate.info.streetName}\n" +
                    additionalAddressText +
                    "${realEstate.info.city}\n" +
                    "${realEstate.info.state} ${realEstate.info.zipcode}\n" +
                    realEstate.info.country,
            poiList = realEstate.poiList.map { it.poiValue.labelId },
            mapUrl = "https://maps.googleapis.com/maps/api/staticmap?" +
                    "&size=400x400" +
                    "&markers=$urlEncodedAddress" +
                    "&key=${BuildConfig.STATIC_MAPS_API_KEY}",
            marketDates = if (realEstate.info.saleDate == null) {
                LocalText.ResWithArgs(
                    stringId = R.string.since_date,
                    args = listOf(realEstate.info.marketEntryDate.format(UtilsRepository.DATE_FORMATTER))
                )
            } else {
                LocalText.ResWithArgs(
                    stringId = R.string.from_sold_date,
                    args = listOf(
                        realEstate.info.marketEntryDate.format(UtilsRepository.DATE_FORMATTER),
                        realEstate.info.saleDate.format(UtilsRepository.DATE_FORMATTER),
                    )
                )
            },
            agentName = if (realEstate.agent != null) {
                LocalText.Simple(content = realEstate.agent.username)
            } else {
                LocalText.Res(stringId = R.string.not_available)
            },
        )
    }
}