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
        DetailInfoViewState(
            description = it.description,
            photoList = it.pictureUriList.map { uri -> DetailInfoViewState.PhotoViewState.Add(uri) }
        )
    }

    fun onDescriptionChanged(description: String?) {
        setFormUseCase.updateDescription(description ?: "")
    }

    fun onPhotoClicked(position: Int) {
        setFormUseCase.setPicturePosition(position)
    }

    fun onPictureTaken(pictureUri: Uri?) {
        if (pictureUri != null) {
            setDisplayedPictureUseCase.init(pictureUri)
        }
    }
}