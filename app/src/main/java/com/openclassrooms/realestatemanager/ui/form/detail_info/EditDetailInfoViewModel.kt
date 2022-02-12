package com.openclassrooms.realestatemanager.ui.form.detail_info

import android.net.Uri
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.domain.form.SetFormUseCase
import com.openclassrooms.realestatemanager.domain.form.GetFormUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditDetailInfoViewModel @Inject constructor(
    getFormUseCase: GetFormUseCase,
    private val setFormUseCase: SetFormUseCase,
) : ViewModel() {

    val viewStateLiveData = Transformations.map(getFormUseCase.getUpdates()) {
        DetailInfoViewState(
            description = it.description,
            photoList = it.pictureUriList.map { uri -> DetailInfoViewState.PhotoViewState.Add(uri) }
        )
    }

    private var whichPhotoPosition = -1

    fun onDescriptionChanged(description: String?) {
        setFormUseCase.updateDescription(description ?: "")
    }

    fun onPhotoClicked(position: Int) {
        whichPhotoPosition = position
    }

    fun onPictureTaken(pictureUri: Uri?) {
        if (pictureUri != null) {
            setFormUseCase.updatePictureUri(whichPhotoPosition, pictureUri)
            whichPhotoPosition = -1
        }
    }
}