package com.openclassrooms.realestatemanager.ui.form.image_launcher

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.openclassrooms.realestatemanager.data.form.ActionRepository
import com.openclassrooms.realestatemanager.data.form.ImagePicker
import com.openclassrooms.realestatemanager.domain.form.SetFormUseCase
import com.openclassrooms.realestatemanager.ui.CoroutineProvider
import com.openclassrooms.realestatemanager.ui.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ImageLauncherViewModel @Inject constructor(
    private val actionRepository: ActionRepository,
    private val setFormUseCase: SetFormUseCase,
    coroutineProvider: CoroutineProvider,
) : ViewModel() {

    private val imageLauncherSingleLiveEvent = SingleLiveEvent<ImageLauncherEvent>()
    val imageLauncherEventLiveData: LiveData<ImageLauncherEvent> = imageLauncherSingleLiveEvent

    init {
        imageLauncherSingleLiveEvent.addSource(
            actionRepository.getImagePickerFlow().asLiveData(coroutineProvider.getIoDispatcher())
        ) {
            if (it != null) {
                actionRepository.flushImagePickerFlow()

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