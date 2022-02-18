package com.openclassrooms.realestatemanager.ui.form.picture

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.data.form.DisplayedPictureEntity
import com.openclassrooms.realestatemanager.domain.form.CheckFormErrorUseCase
import com.openclassrooms.realestatemanager.domain.form.GetFormUseCase
import com.openclassrooms.realestatemanager.domain.form.SetFormUseCase
import com.openclassrooms.realestatemanager.ui.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PictureViewModel @Inject constructor(
    getFormUseCase: GetFormUseCase,
    private val checkFormErrorUseCase: CheckFormErrorUseCase,
    private val setFormUseCase: SetFormUseCase,
) : ViewModel() {

    private val viewStateMediatorLiveData = MediatorLiveData<DisplayedPictureEntity>()
    val viewStateLiveData: LiveData<DisplayedPictureEntity> = viewStateMediatorLiveData
    private val exitSingleLiveEvent = SingleLiveEvent<Unit>()
    val exitEventLiveData: LiveData<Unit> = exitSingleLiveEvent

    init {
        viewStateMediatorLiveData.addSource(getFormUseCase.getDisplayedPicture()) {
            if (it != null) {
                viewStateMediatorLiveData.value = it
            }
        }
    }

    fun onDescriptionChanged(description: String?, cursorPosition: Int) {
        setFormUseCase.updatePictureDescription(description ?: "", cursorPosition)
    }

    fun onSaveMenuItemClicked() {
        if (checkFormErrorUseCase.containsNoError(CheckFormErrorUseCase.PageToCheck.PICTURE)) {
            setFormUseCase.savePicture()
            exitSingleLiveEvent.call()
        }
    }

    fun onFragmentViewDestroyed() {
        setFormUseCase.resetPicture()
    }
}