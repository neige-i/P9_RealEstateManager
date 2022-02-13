package com.openclassrooms.realestatemanager.ui.form.picture

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.data.form.DisplayedPictureEntity
import com.openclassrooms.realestatemanager.domain.displayed_picture.GetDisplayedPictureUseCase
import com.openclassrooms.realestatemanager.domain.displayed_picture.SavePictureUseCase
import com.openclassrooms.realestatemanager.domain.displayed_picture.SetDisplayedPictureUseCase
import com.openclassrooms.realestatemanager.domain.form.CheckFormErrorUseCase
import com.openclassrooms.realestatemanager.ui.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PictureViewModel @Inject constructor(
    getDisplayedPictureUseCase: GetDisplayedPictureUseCase,
    private val checkFormErrorUseCase: CheckFormErrorUseCase,
    private val savePictureUseCase: SavePictureUseCase,
    private val setDisplayedPictureUseCase: SetDisplayedPictureUseCase,
) : ViewModel() {

    private val viewStateMediatorLiveData = MediatorLiveData<DisplayedPictureEntity>()
    val viewStateLiveData: LiveData<DisplayedPictureEntity> = viewStateMediatorLiveData
    private val exitSingleLiveEvent = SingleLiveEvent<Unit>()
    val exitEvent: LiveData<Unit> = exitSingleLiveEvent

    init {
        viewStateMediatorLiveData.addSource(getDisplayedPictureUseCase()) {
            if (it != null) {
                viewStateMediatorLiveData.value = it
            }
        }
    }

    fun onDescriptionChanged(description: String?) {
        setDisplayedPictureUseCase.updateDescription(description ?: "")
    }

    fun onSaveMenuItemClicked() {
        if (checkFormErrorUseCase.containsNoError(CheckFormErrorUseCase.PageToCheck.PICTURE)) {
            savePictureUseCase()
            exitSingleLiveEvent.call()
        }
    }

    fun onActivityFinished() {
        setDisplayedPictureUseCase.reset()
    }
}