package com.openclassrooms.realestatemanager.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.domain.form.SetFormUseCase
import com.openclassrooms.realestatemanager.domain.real_estate.GetCurrentEstateUseCase
import com.openclassrooms.realestatemanager.domain.real_estate.RealEstateResult
import com.openclassrooms.realestatemanager.ui.util.CoroutineProvider
import com.openclassrooms.realestatemanager.ui.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DetailParentViewModel @Inject constructor(
    private val getCurrentEstateUseCase: GetCurrentEstateUseCase,
    private val setFormUseCase: SetFormUseCase,
    private val coroutineProvider: CoroutineProvider,
) : ViewModel() {

    private val detailSingleLiveEvent = SingleLiveEvent<DetailEvent>()
    val detailEventLiveData: LiveData<DetailEvent> = detailSingleLiveEvent

    fun onActivityResumed(isTablet: Boolean) {
        // Do not display DetailActivity in tablet configuration
        if (isTablet) {
            detailSingleLiveEvent.value = DetailEvent.Exit
        }
    }

    fun onEditMenuItemClicked() {
        viewModelScope.launch(coroutineProvider.getIoDispatcher()) {
            val currentEstateResult = getCurrentEstateUseCase().first()

            if (currentEstateResult is RealEstateResult.Success) {
                setFormUseCase.initForm(currentEstateResult.realEstate)

                withContext(coroutineProvider.getMainDispatcher()) {
                    detailSingleLiveEvent.value = DetailEvent.OpenForm
                }
            }
        }
    }
}