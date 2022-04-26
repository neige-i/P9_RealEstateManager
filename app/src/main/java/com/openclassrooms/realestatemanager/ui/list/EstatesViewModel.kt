package com.openclassrooms.realestatemanager.ui.list

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.RealEstateType
import com.openclassrooms.realestatemanager.data.ResourcesRepository
import com.openclassrooms.realestatemanager.data.real_estate.CurrentEstateRepository
import com.openclassrooms.realestatemanager.data.real_estate.RealEstateRepository
import com.openclassrooms.realestatemanager.ui.util.CoroutineProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import java.text.NumberFormat
import javax.inject.Inject

@HiltViewModel
class EstatesViewModel @Inject constructor(
    realEstateRepository: RealEstateRepository,
    private val currentEstateRepository: CurrentEstateRepository,
    resourcesRepository: ResourcesRepository,
    coroutineProvider: CoroutineProvider,
    application: Application,
    numberFormat: NumberFormat,
) : ViewModel() {

    val viewStateLiveData: LiveData<List<EstateViewState>> = combine(
        realEstateRepository.getAllRealEstates(),
        currentEstateRepository.getIdOrNull(),
        resourcesRepository.isTabletFlow(),
    ) { allEstates, currentEstateId, isTablet ->

        allEstates.map { realEstate ->
            EstateViewState(
                id = realEstate.info.realEstateId,
                photoUrl = realEstate.photoList.first().uri,
                type = application.getString(RealEstateType.valueOf(realEstate.info.type).labelId),
                city = realEstate.info.city,
                price = realEstate.info.price?.let { price ->
                    application.getString(R.string.price_in_dollars, numberFormat.format(price))
                } ?: application.getString(R.string.undefined_price),
                style = if (currentEstateId == realEstate.info.realEstateId && isTablet) {
                    EstateViewState.Style(
                        backgroundColor = R.color.colorAccent,
                        priceTextColor = android.R.color.white,
                        cityTextColor = android.R.color.black,
                    )
                } else {
                    EstateViewState.Style(
                        backgroundColor = android.R.color.white,
                        priceTextColor = R.color.colorAccent,
                        cityTextColor = android.R.color.tab_indicator_text,
                    )
                },
            )
        }
    }.asLiveData(coroutineProvider.getIoDispatcher())

    fun onEstateClicked(estateId: Long) {
        currentEstateRepository.setId(estateId)
    }
}