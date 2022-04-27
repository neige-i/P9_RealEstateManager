package com.openclassrooms.realestatemanager.ui.form.detail_info

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.domain.form.GetFormUseCase
import com.openclassrooms.realestatemanager.domain.form.SetFormUseCase
import com.openclassrooms.realestatemanager.ui.util.CoroutineProvider
import com.openclassrooms.realestatemanager.ui.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class EditDetailInfoViewModel @Inject constructor(
    getFormUseCase: GetFormUseCase,
    private val setFormUseCase: SetFormUseCase,
    coroutineProvider: CoroutineProvider,
) : ViewModel() {

    val viewStateLiveData = getFormUseCase.getFormFlow().map { form ->
        DetailInfoViewState(
            description = form.description,
            descriptionSelection = form.descriptionCursor,
            photoList = form.pictureList
                .map { picture ->
                    DetailInfoViewState.PhotoViewState.Photo(
                        uri = picture.uri,
                        description = picture.description
                    ) as DetailInfoViewState.PhotoViewState
                }
                .toMutableList()
                .apply { add(DetailInfoViewState.PhotoViewState.Add) },
        )
    }.asLiveData(coroutineProvider.getIoDispatcher())

    private val showErrorSingleLiveEvent = SingleLiveEvent<String>()
    val showErrorEventLiveData: LiveData<String> = showErrorSingleLiveEvent

    init {
        viewModelScope.launch(coroutineProvider.getIoDispatcher()) {
            getFormUseCase.getFormFlow()
                .map { it.pictureListError }
                .filterNotNull()
                .collect { errorMessage ->

                    withContext(coroutineProvider.getMainDispatcher()) {
                        showErrorSingleLiveEvent.value = errorMessage
                        setFormUseCase.resetPictureError()
                    }
                }
        }
    }

    fun onDescriptionChanged(description: String?, cursorPosition: Int) {
        setFormUseCase.updateDescription(description ?: "", cursorPosition)
    }

    fun onPhotoAdded(position: Int) {
        setFormUseCase.updatePicturePosition(position)
    }

    fun onPhotoOpened(position: Int, photo: DetailInfoViewState.PhotoViewState.Photo) {
        setFormUseCase.updatePicturePosition(position)
        setFormUseCase.initPicture(photo.uri, photo.description)
    }

    fun onPhotoRemoved(position: Int) {
        setFormUseCase.removePictureAt(position)
    }
}