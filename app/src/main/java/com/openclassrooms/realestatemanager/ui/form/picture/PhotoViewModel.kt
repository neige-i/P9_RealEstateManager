package com.openclassrooms.realestatemanager.ui.form.picture

import android.net.Uri
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.current_photo.CurrentPhotoRepository
import com.openclassrooms.realestatemanager.domain.form.CheckFormErrorUseCase
import com.openclassrooms.realestatemanager.domain.form.CheckFormErrorUseCase.PageToCheck
import com.openclassrooms.realestatemanager.domain.form.SetFormUseCase
import com.openclassrooms.realestatemanager.ui.util.CoroutineProvider
import com.openclassrooms.realestatemanager.ui.util.LocalText
import com.openclassrooms.realestatemanager.ui.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PhotoViewModel @Inject constructor(
    private val currentPhotoRepository: CurrentPhotoRepository,
    private val checkFormErrorUseCase: CheckFormErrorUseCase,
    private val setFormUseCase: SetFormUseCase,
    private val coroutineProvider: CoroutineProvider,
) : ViewModel() {

    private var isFirstInitialization = true

    val viewStateLiveData: LiveData<PhotoViewState> = liveData(coroutineProvider.getIoDispatcher()) {
        currentPhotoRepository.getCurrentPhotoFlow().collect { currentPhoto ->
            if (currentPhoto == CurrentPhotoRepository.DEFAULT_PHOTO && isFirstInitialization) {
                isFirstInitialization = false
                withContext(coroutineProvider.getMainDispatcher()) {
                    showImagePickerDialog()
                }
            } else {
                emit(
                    PhotoViewState(
                        uri = currentPhoto.uri,
                        isUriErrorVisible = currentPhoto.isUriErrorVisible,
                        description = currentPhoto.description,
                        descriptionError = currentPhoto.descriptionError,
                    )
                )
            }
        }
    }

    private val photoSingleLiveEvent = SingleLiveEvent<PhotoEvent>()
    val photoEventLiveData: LiveData<PhotoEvent> = photoSingleLiveEvent

    fun onPhotoPicked(uri: Uri?, success: Boolean = true) {
        if (uri != null && success) {
            currentPhotoRepository.setUri(photoUri = uri)
        }
    }

    fun onDescriptionChanged(description: String?) {
        currentPhotoRepository.setDescription(description ?: "")
    }

    fun onPickerDialogItemClicked(position: Int) {
        photoSingleLiveEvent.value = when (ImagePicker.values()[position]) {
            ImagePicker.GALLERY -> PhotoEvent.OpenGallery
            ImagePicker.CAMERA -> PhotoEvent.OpenCamera
        }
    }

    fun onSaveMenuItemClicked() {
        viewModelScope.launch(coroutineProvider.getIoDispatcher()) {
            if (checkFormErrorUseCase.containsNoError(PageToCheck.PICTURE)) {
                setFormUseCase.savePicture()

                withContext(coroutineProvider.getMainDispatcher()) {
                    photoSingleLiveEvent.value = PhotoEvent.Exit
                }
            }
        }
    }

    fun onEditMenuItemClicked() {
        showImagePickerDialog()
    }

    private fun showImagePickerDialog() {
        photoSingleLiveEvent.value = PhotoEvent.ShowPickerDialog(
            items = ImagePicker.values().map { imagePicker ->
                LocalText.Res(stringId = imagePicker.labelId)
            }
        )
    }

    override fun onCleared() {
        currentPhotoRepository.resetPhotoIndex()
    }

    enum class ImagePicker(@StringRes val labelId: Int) {
        GALLERY(R.string.image_picker_dialog_gallery_item),
        CAMERA(R.string.image_picker_dialog_camera_item),
    }
}