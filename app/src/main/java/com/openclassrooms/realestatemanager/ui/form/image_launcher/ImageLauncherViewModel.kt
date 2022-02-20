package com.openclassrooms.realestatemanager.ui.form.image_launcher

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.data.form.ImagePicker
import com.openclassrooms.realestatemanager.domain.form.GetFormRequestUseCase
import com.openclassrooms.realestatemanager.domain.form.SetFormRequestUseCase
import com.openclassrooms.realestatemanager.domain.form.SetFormUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ImageLauncherViewModel @Inject constructor(
    getFormRequestUseCase: GetFormRequestUseCase,
    private val setFormUseCase: SetFormUseCase,
    private val setFormRequestUseCase: SetFormRequestUseCase,
) : ViewModel() {

    private val imageLauncherSingleLiveEvent = MediatorLiveData<ImageLauncherEvent>()
    val imageLauncherEventLiveData: LiveData<ImageLauncherEvent> = imageLauncherSingleLiveEvent

    init {
        imageLauncherSingleLiveEvent.addSource(getFormRequestUseCase.getImagePicker()) {
            if (it != null) {
                setFormRequestUseCase.pickImage(null) // Reset flag

                imageLauncherSingleLiveEvent.value = when (it) {
                    ImagePicker.GALLERY -> ImageLauncherEvent.OpenGallery
                    ImagePicker.CAMERA -> ImageLauncherEvent.OpenCamera
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