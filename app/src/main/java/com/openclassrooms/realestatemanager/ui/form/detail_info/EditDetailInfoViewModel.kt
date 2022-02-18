package com.openclassrooms.realestatemanager.ui.form.detail_info

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.domain.form.GetFormUseCase
import com.openclassrooms.realestatemanager.domain.form.SetFormUseCase
import com.openclassrooms.realestatemanager.ui.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditDetailInfoViewModel @Inject constructor(
    getFormUseCase: GetFormUseCase,
    private val setFormUseCase: SetFormUseCase,
) : ViewModel() {

    val viewStateLiveData = Transformations.map(getFormUseCase.getUpdates()) {

        it.pictureListError?.let { errorMessage ->
            showErrorSingleLiveEvent.value = errorMessage
            setFormUseCase.resetPictureError()
        }

        val photoList = mutableListOf<DetailInfoViewState.PhotoViewState>()
        it.pictureList.forEach { picture ->
            photoList.add(
                DetailInfoViewState.PhotoViewState.Picture(
                    uri = picture.uri,
                    description = picture.description
                )
            )
        }
        photoList.add(DetailInfoViewState.PhotoViewState.Add)

        DetailInfoViewState(
            description = it.description,
            descriptionSelection = it.descriptionCursor,
            photoList = photoList
        )
    }
    private val showErrorSingleLiveEvent = SingleLiveEvent<String>()
    val showErrorEventLiveData: LiveData<String> = showErrorSingleLiveEvent

    fun onDescriptionChanged(description: String?, cursorPosition: Int) {
        setFormUseCase.updateDescription(description ?: "", cursorPosition)
    }

    fun onPhotoAdded(position: Int) {
        setFormUseCase.setPicturePosition(position)
    }

    fun onPhotoOpened(position: Int, picture: DetailInfoViewState.PhotoViewState.Picture) {
        setFormUseCase.setPicturePosition(position)
        setFormUseCase.initPicture(picture.uri, picture.description)
    }

    fun onPhotoRemoved(position: Int) {
        setFormUseCase.removePhoto(position)
    }
}