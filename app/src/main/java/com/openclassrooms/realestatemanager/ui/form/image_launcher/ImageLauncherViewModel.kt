package com.openclassrooms.realestatemanager.ui.form.image_launcher

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.data.form.FormRepository
import com.openclassrooms.realestatemanager.domain.form.GetFormUseCase
import com.openclassrooms.realestatemanager.domain.form.SetFormUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ImageLauncherViewModel @Inject constructor(
    getFormUseCase: GetFormUseCase,
    private val setFormUseCase: SetFormUseCase,
) : ViewModel() {

    private val imageLauncherSingleLiveEvent = MediatorLiveData<ImageLauncherEvent>()
    val imageLauncherEventLiveData: LiveData<ImageLauncherEvent> = imageLauncherSingleLiveEvent

    init {
        imageLauncherSingleLiveEvent.addSource(getFormUseCase.getPicturePicker()) {
            if (it != null) {
                setFormUseCase.setPicturePicker(null) // Reset flag

                imageLauncherSingleLiveEvent.value = when (it) {
                    FormRepository.PicturePicker.GALLERY -> ImageLauncherEvent.OpenGallery
                    FormRepository.PicturePicker.CAMERA -> ImageLauncherEvent.OpenCamera
                }
            }
        }
    }

    fun onPhotoPicked(uri: Uri?, success: Boolean = true) {
        if (uri != null && success) {
            setFormUseCase.setPictureUri(uri)
        }
    }
}