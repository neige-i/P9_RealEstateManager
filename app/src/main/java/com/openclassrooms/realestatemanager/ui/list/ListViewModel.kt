package com.openclassrooms.realestatemanager.ui.list

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.RealEstateType
import com.openclassrooms.realestatemanager.data.real_estate.CurrentEstateRepository
import com.openclassrooms.realestatemanager.data.real_estate.RealEstateRepository
import com.openclassrooms.realestatemanager.ui.util.CoroutineProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.combine
import java.text.NumberFormat
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    realEstateRepository: RealEstateRepository,
    private val currentEstateRepository: CurrentEstateRepository,
    coroutineProvider: CoroutineProvider,
    application: Application,
    numberFormat: NumberFormat,
) : ViewModel() {

    private val pingMutableSharedFlow = MutableSharedFlow<Boolean>(replay = 1)

    val viewState: LiveData<List<RealEstateViewState>> = combine(
        realEstateRepository.getAllRealEstates(),
        currentEstateRepository.getIdOrNull(),
        pingMutableSharedFlow,
    ) { allEstates, currentEstateId, _ ->

        allEstates.map { realEstate ->
            val isCurrentEstateSelected = currentEstateId == realEstate.info.realEstateId &&
                    application.resources.getBoolean(R.bool.is_tablet)

            RealEstateViewState(
                id = realEstate.info.realEstateId,
                photoUrl = realEstate.photoList.first().uri,
                type = application.getString(RealEstateType.valueOf(realEstate.info.type).labelId),
                city = realEstate.info.city,
                price = realEstate.info.price?.let { price ->
                    application.getString(R.string.price_in_dollars, numberFormat.format(price))
                } ?: application.getString(R.string.undefined_price),
                backgroundColor = if (isCurrentEstateSelected) {
                    R.color.colorAccent
                } else {
                    android.R.color.white
                },
                priceTextColor = if (isCurrentEstateSelected) {
                    android.R.color.white
                } else {
                    R.color.colorAccent
                },
                cityTextColor = if (isCurrentEstateSelected) {
                    android.R.color.black
                } else {
                    android.R.color.tab_indicator_text
                }
            )
        }
    }.asLiveData(coroutineProvider.getIoDispatcher())

    fun onFragmentResumed() {
        pingMutableSharedFlow.tryEmit(true)
    }

    fun onItemClicked(realEstateId: Long) {
        currentEstateRepository.setId(realEstateId)
    }
}