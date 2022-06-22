package com.openclassrooms.realestatemanager.ui.form.picture

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.openclassrooms.realestatemanager.data.form.CurrentPictureRepository
import com.openclassrooms.realestatemanager.domain.form.CheckFormErrorUseCase
import com.openclassrooms.realestatemanager.domain.form.CheckFormErrorUseCase.PageToCheck
import com.openclassrooms.realestatemanager.domain.form.SetFormUseCase
import com.openclassrooms.realestatemanager.ui.util.CoroutineProvider
import com.openclassrooms.realestatemanager.ui.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class PictureViewModel @Inject constructor(
    currentPictureRepository: CurrentPictureRepository,
    private val checkFormErrorUseCase: CheckFormErrorUseCase,
    private val setFormUseCase: SetFormUseCase,
    coroutineProvider: CoroutineProvider,
) : ViewModel() {

    val viewStateLiveData: LiveData<PictureViewState> = currentPictureRepository.getPictureFlow()
        .map {
            PictureViewState(
                uri = it.uri,
                description = it.description,
                descriptionError = it.descriptionError,
            )
        }
        .asLiveData(coroutineProvider.getIoDispatcher())

    private val exitSingleLiveEvent = SingleLiveEvent<Unit>()
    val exitEventLiveData: LiveData<Unit> = exitSingleLiveEvent

    fun onDescriptionChanged(description: String?) {
        setFormUseCase.updatePictureDescription(description ?: "")
    }

    fun onSaveMenuItemClicked() {
        if (checkFormErrorUseCase.containsNoError(PageToCheck.PICTURE)) {
            setFormUseCase.savePicture()
            exitSingleLiveEvent.call()
        }
    }

    override fun onCleared() {
        setFormUseCase.resetPicture()
    }
}