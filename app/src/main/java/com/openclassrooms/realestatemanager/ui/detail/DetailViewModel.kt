package com.openclassrooms.realestatemanager.ui.detail

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.PointOfInterest
import com.openclassrooms.realestatemanager.data.agent.AgentRepository
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
    agentRepository: AgentRepository,
    coroutineProvider: CoroutineProvider,
    application: Application,
) : ViewModel() {

    val viewState: LiveData<DetailViewState> = getCurrentEstateUseCase.invoke().map {
        when (it) {
            is RealEstateResult.Success -> getInfoViewState(
                realEstate = it.realEstate,
                agentRepository = agentRepository,
                application = application
            )
            is RealEstateResult.Failure -> DetailViewState.Empty(
                noSelectionLabelText = application.getString(R.string.unknown_real_estate_selected, it.estateId)
            )
            RealEstateResult.Idle -> DetailViewState.Empty(
                noSelectionLabelText = application.getString(R.string.no_real_estate_selected)
            )
        }
    }.asLiveData(coroutineProvider.getIoDispatcher())

    private fun getInfoViewState(
        realEstate: RealEstateEntity,
        agentRepository: AgentRepository,
        application: Application
    ): DetailViewState.Info {
        val availableForSale = realEstate.saleDate == null

        val additionalAddressText = if (realEstate.additionalAddressInfo.isNotEmpty()) {
            "${realEstate.additionalAddressInfo}\n"
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
            photoList = realEstate.pictureList
                .map {
                    DetailViewState.Info.Photo(
                        url = it.key,
                        description = it.value
                    )
                },
            description = realEstate.description
                .ifEmpty {
                    application.getString(R.string.not_available)
                },
            surface = realEstate.area?.toString() ?: application.getString(R.string.not_available),
            roomCount = realEstate.totalRoomCount.toString(),
            bathroomCount = realEstate.bathroomCount.toString(),
            bedroomCount = realEstate.bedroomCount.toString(),
            address = "${realEstate.streetName}\n" +
                    additionalAddressText +
                    "${realEstate.city}\n" +
                    "${realEstate.state} ${realEstate.zipcode}\n" +
                    realEstate.country,
            poiList = realEstate.pointsOfInterests.map { PointOfInterest.valueOf(it).labelId },
            market_dates = if (availableForSale) {
                application.getString(R.string.since_date, realEstate.marketEntryDate)
            } else {
                application.getString(
                    R.string.from_sold_date,
                    realEstate.marketEntryDate,
                    realEstate.saleDate
                )
            },
            agentName = if (realEstate.agentId != null) {
                agentRepository
                    .getAgentById(realEstate.agentId)
                    ?.name
                    ?: application.getString(R.string.unknown_agent)
            } else {
                application.getString(R.string.not_available)
            }
        )
    }

    fun onMediaClicked(photo: DetailViewState.Info.Photo) {
        Log.d("Neige", "onMediaClicked() called with: photo = $photo")
    }
}