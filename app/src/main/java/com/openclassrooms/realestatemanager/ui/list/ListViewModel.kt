package com.openclassrooms.realestatemanager.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.openclassrooms.realestatemanager.data.real_estate.CurrentEstateRepository
import com.openclassrooms.realestatemanager.data.real_estate.RealEstateRepository
import com.openclassrooms.realestatemanager.ui.util.CoroutineProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    realEstateRepository: RealEstateRepository,
    private val currentEstateRepository: CurrentEstateRepository,
    coroutineProvider: CoroutineProvider,
) : ViewModel() {

    val viewState: LiveData<List<String>> = realEstateRepository.getAllEstates()
        .map {
            it.map { realEstate -> realEstate.id.toString() }
        }.asLiveData(coroutineProvider.getIoDispatcher())

    fun onItemClicked(item: String) {
        currentEstateRepository.setCurrentEstateId(item.toLong())
    }
}