package com.openclassrooms.realestatemanager.ui.form.detail_info

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.openclassrooms.realestatemanager.domain.form.GetFormUseCase
import com.openclassrooms.realestatemanager.domain.form.SetFormUseCase
import com.openclassrooms.realestatemanager.ui.util.CoroutineProvider
import com.openclassrooms.realestatemanager.ui.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class EditDetailInfoViewModel @Inject constructor(
    getFormUseCase: GetFormUseCase,
    private val setFormUseCase: SetFormUseCase,
    coroutineProvider: CoroutineProvider,
) : ViewModel() {

    val viewStateLiveData = getFormUseCase.getFormFlow().map {

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
    }.asLiveData(coroutineProvider.getIoDispatcher())
    private val showErrorSingleLiveEvent = SingleLiveEvent<String>()
    val showErrorEventLiveData: LiveData<String> = showErrorSingleLiveEvent

    init {
        showErrorSingleLiveEvent.addSource(
            getFormUseCase.getFormFlow().asLiveData(coroutineProvider.getIoDispatcher())
        ) {
            it.pictureListError?.let { errorMessage ->
                showErrorSingleLiveEvent.value = errorMessage
                setFormUseCase.resetPictureError()
            }
        }
    }

    fun onDescriptionChanged(description: String?, cursorPosition: Int) {
        setFormUseCase.updateDescription(description ?: "", cursorPosition)
    }

    fun onPhotoAdded(position: Int) {
        setFormUseCase.updatePicturePosition(position)
    }

    fun onPhotoOpened(position: Int, picture: DetailInfoViewState.PhotoViewState.Picture) {
        setFormUseCase.updatePicturePosition(position)
        setFormUseCase.initPicture(picture.uri, picture.description)
    }

    fun onPhotoRemoved(position: Int) {
        setFormUseCase.removePictureAt(position)
    }
}