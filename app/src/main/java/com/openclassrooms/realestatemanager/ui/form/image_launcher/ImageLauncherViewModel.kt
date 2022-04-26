package com.openclassrooms.realestatemanager.ui.form.image_launcher

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.data.form.FormRepository
import com.openclassrooms.realestatemanager.data.form.ImagePicker
import com.openclassrooms.realestatemanager.domain.form.SetFormUseCase
import com.openclassrooms.realestatemanager.ui.util.CoroutineProvider
import com.openclassrooms.realestatemanager.ui.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ImageLauncherViewModel @Inject constructor(
    formRepository: FormRepository,
    private val setFormUseCase: SetFormUseCase,
    coroutineProvider: CoroutineProvider,
) : ViewModel() {

    private val imageLauncherSingleLiveEvent = SingleLiveEvent<ImageLauncherEvent>()
    val imageLauncherEventLiveData: LiveData<ImageLauncherEvent> = imageLauncherSingleLiveEvent

    init {
        viewModelScope.launch(coroutineProvider.getIoDispatcher()) {
            formRepository.getImagePickerFlow().collect {

                withContext(coroutineProvider.getMainDispatcher()) {
                    imageLauncherSingleLiveEvent.value = when (it) {
                        ImagePicker.GALLERY -> ImageLauncherEvent.OpenGallery
                        ImagePicker.CAMERA -> ImageLauncherEvent.OpenCamera
                    }
                }
            }
        }
    }

    fun onPhotoPicked(uri: Uri?, success: Boolean = true) {
        if (uri != null && success) {
            setFormUseCase.initPicture(uri = uri)
        }
    }
}