package com.openclassrooms.realestatemanager.ui.form.picture

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.openclassrooms.realestatemanager.domain.form.CheckFormErrorUseCase
import com.openclassrooms.realestatemanager.domain.form.CheckFormErrorUseCase.PageToCheck
import com.openclassrooms.realestatemanager.domain.form.GetFormUseCase
import com.openclassrooms.realestatemanager.domain.form.SetFormUseCase
import com.openclassrooms.realestatemanager.ui.CoroutineProvider
import com.openclassrooms.realestatemanager.ui.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class PictureViewModel @Inject constructor(
    getFormUseCase: GetFormUseCase,
    private val checkFormErrorUseCase: CheckFormErrorUseCase,
    private val setFormUseCase: SetFormUseCase,
    coroutineProvider: CoroutineProvider,
) : ViewModel() {

    val viewStateLiveData: LiveData<PictureViewState> = getFormUseCase.getCurrentPictureFlow()
        .map {
            PictureViewState(
                uri = it.uri,
                description = it.description,
                descriptionError = it.descriptionError,
                descriptionSelection = it.descriptionCursor
            )
        }
        .asLiveData(coroutineProvider.getIoDispatcher())

    private val exitSingleLiveEvent = SingleLiveEvent<Unit>()
    val exitEventLiveData: LiveData<Unit> = exitSingleLiveEvent

    fun onDescriptionChanged(description: String?, cursorPosition: Int) {
        setFormUseCase.updatePictureDescription(description ?: "", cursorPosition)
    }

    fun onSaveMenuItemClicked() {
        if (checkFormErrorUseCase.containsNoError(PageToCheck.PICTURE)) {
            setFormUseCase.savePicture()
            exitSingleLiveEvent.call()
        }
    }

    fun onActivityFinished() {
        setFormUseCase.resetPicture()
    }
}