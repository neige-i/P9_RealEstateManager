package com.openclassrooms.realestatemanager.ui.form.detail_info

import android.net.Uri
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.domain.displayed_picture.SetDisplayedPictureUseCase
import com.openclassrooms.realestatemanager.domain.form.GetFormUseCase
import com.openclassrooms.realestatemanager.domain.form.SetFormUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditDetailInfoViewModel @Inject constructor(
    getFormUseCase: GetFormUseCase,
    private val setFormUseCase: SetFormUseCase,
    private val setDisplayedPictureUseCase: SetDisplayedPictureUseCase,
) : ViewModel() {

    val viewStateLiveData = Transformations.map(getFormUseCase.getUpdates()) {

        val photoList = mutableListOf<DetailInfoViewState.PhotoViewState>()
        it.pictureList.forEach { picture ->
            photoList.add(DetailInfoViewState.PhotoViewState.Picture(
                uri = picture.uri,
                description = picture.description
            ))
        }
        photoList.add(DetailInfoViewState.PhotoViewState.Add)

        DetailInfoViewState(
            description = it.description,
            photoList = photoList
        )
    }

    fun onDescriptionChanged(description: String?) {
        setFormUseCase.updateDescription(description ?: "")
    }

    fun onPhotoAdded(position: Int) {
        setFormUseCase.setPicturePosition(position)
    }

    fun onPhotoPicked(pictureUri: Uri?) {
        if (pictureUri != null) {
            setDisplayedPictureUseCase.init(pictureUri)
        }
    }

    fun onPhotoOpened(position: Int, picture: DetailInfoViewState.PhotoViewState.Picture) {
        setFormUseCase.setPicturePosition(position)
        setDisplayedPictureUseCase.init(picture.uri, picture.description)
    }

    fun onPhotoRemoved(position: Int) {
        setFormUseCase.removePhoto(position)
    }
}