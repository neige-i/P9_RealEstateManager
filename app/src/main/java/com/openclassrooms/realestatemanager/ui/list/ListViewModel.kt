package com.openclassrooms.realestatemanager.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.ResourcesRepository
import com.openclassrooms.realestatemanager.data.real_estate.CurrentEstateRepository
import com.openclassrooms.realestatemanager.domain.filter.GetFilteredEstatesUseCase
import com.openclassrooms.realestatemanager.ui.util.CoroutineProvider
import com.openclassrooms.realestatemanager.ui.util.LocalText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import java.text.NumberFormat
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    getFilteredEstatesUseCase: GetFilteredEstatesUseCase,
    currentEstateRepository: CurrentEstateRepository,
    resourcesRepository: ResourcesRepository,
    coroutineProvider: CoroutineProvider,
    numberFormat: NumberFormat,
) : ViewModel() {

    val viewStateLiveData: LiveData<List<EstateViewState>> = combine(
        getFilteredEstatesUseCase(),
        currentEstateRepository.getIdOrNull(),
        resourcesRepository.isTabletFlow(),
    ) { filteredEstates, currentEstateId, isTablet ->

        filteredEstates.map { realEstate ->
            val estateId = realEstate.info.estateId

            EstateViewState(
                id = estateId,
                photoUrl = realEstate.photoList.first().uri,
                type = realEstate.info.type.labelId,
                city = realEstate.info.city,
                price = if (realEstate.info.price != null) {
                    LocalText.ResWithArgs(
                        stringId = R.string.price_in_dollars,
                        args = listOf(numberFormat.format(realEstate.info.price))
                    )
                } else {
                    LocalText.Res(stringId = R.string.undefined_price)
                },
                style = if (currentEstateId == estateId && isTablet) {
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
                onClicked = { currentEstateRepository.setId(estateId) }
            )
        }
    }.asLiveData(coroutineProvider.getIoDispatcher())
}