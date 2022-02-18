package com.openclassrooms.realestatemanager.ui.form

import android.app.Application
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.form.DisplayedPictureEntity
import com.openclassrooms.realestatemanager.data.form.FormInfoEntity
import com.openclassrooms.realestatemanager.data.form.FormRepository
import com.openclassrooms.realestatemanager.domain.form.CheckFormErrorUseCase
import com.openclassrooms.realestatemanager.domain.form.GetFormUseCase
import com.openclassrooms.realestatemanager.domain.form.SetFormUseCase
import com.openclassrooms.realestatemanager.ui.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FormViewModel @Inject constructor(
    getFormUseCase: GetFormUseCase,
    private val checkFormErrorUseCase: CheckFormErrorUseCase,
    private val setFormUseCase: SetFormUseCase,
    private val application: Application,
) : ViewModel() {

    private val viewStateMediatorLiveData = MediatorLiveData<String>()
    val viewStateLiveData: LiveData<String> = viewStateMediatorLiveData
    private val formSingleLiveEvent = SingleLiveEvent<FormEvent>()
    val formEventLiveData: LiveData<FormEvent> = formSingleLiveEvent

    private val backStackEntryCountMutableLiveData = MutableLiveData<Int>()

    private lateinit var currentFormInfo: FormInfoEntity
    private var checkDraft = true

    init {
        val formInfoLiveData = getFormUseCase.getWithInfo()
        viewStateMediatorLiveData.addSource(formInfoLiveData) {
            combineViewState(it, backStackEntryCountMutableLiveData.value)
        }
        viewStateMediatorLiveData.addSource(backStackEntryCountMutableLiveData) {
            combineViewState(formInfoLiveData.value, it)
        }

        val displayedPictureLiveData = getFormUseCase.getDisplayedPicture()
        formSingleLiveEvent.addSource(displayedPictureLiveData) {
            combineDisplayedPicture(it, backStackEntryCountMutableLiveData.value)
        }
        formSingleLiveEvent.addSource(backStackEntryCountMutableLiveData) {
            combineDisplayedPicture(displayedPictureLiveData.value, it)
        }

        formSingleLiveEvent.addSource(getFormUseCase.getExitFormRequest()) {
            if (it) {
                setFormUseCase.setExitRequest(false) // Reset flag
                formSingleLiveEvent.value = FormEvent.ExitActivity
            }
        }

        formSingleLiveEvent.addSource(getFormUseCase.getPicturePicker()) {
            it?.let {
                setFormUseCase.setPicturePicker(null) // Reset flag

                formSingleLiveEvent.value = when (it) {
                    FormRepository.PicturePicker.GALLERY -> FormEvent.OpenGallery
                    FormRepository.PicturePicker.CAMERA -> FormEvent.OpenCamera
                }
            }
        }
    }

    private fun combineViewState(formInfo: FormInfoEntity?, backStackEntryCount: Int?) {
        if (formInfo == null) {
            return
        }

        currentFormInfo = formInfo

        checkExistingDraft(formInfo)

        viewStateMediatorLiveData.value =
            if (backStackEntryCount == null || backStackEntryCount == 0) {
                application.getString(R.string.toolbar_title_add) +
                        " (" + (formInfo.form.displayedPage + 1) +
                        "/" + formInfo.pageCount +
                        ")"
            } else {
                application.getString(R.string.toolbar_title_edit_picture)
            }
    }

    private fun combineDisplayedPicture(
        displayedPicture: DisplayedPictureEntity?,
        backStackEntryCount: Int?,
    ) {
        val isPagerDisplayed = backStackEntryCount == null || backStackEntryCount == 0
        if (displayedPicture != null && isPagerDisplayed) {
            formSingleLiveEvent.value = FormEvent.ShowPicture
        }
    }

    private fun checkExistingDraft(formInfo: FormInfoEntity) {
        if (checkDraft && formInfo.type == FormInfoEntity.FormType.ADD && formInfo.hasModifications) {
            formSingleLiveEvent.value = FormEvent.ShowDialog(
                type = DialogType.DRAFT,
                title = application.getString(R.string.draft_form_dialog_title),
                message = application.getString(
                    R.string.draft_form_dialog_message,
                    formInfo.form.type
                ),
                positiveButtonText = application.getString(R.string.draft_form_dialog_positive_button),
                negativeButtonText = application.getString(R.string.draft_form_dialog_negative_button)
            )
        }

        // Reset flag to display draft dialog only once (when the ViewModel is initialized)
        checkDraft = false
    }

    fun onBackStackChanged(backStackEntryCount: Int) {
        backStackEntryCountMutableLiveData.value = backStackEntryCount
    }

    fun onGoBack(backStackEntryCount: Int) {
        if (backStackEntryCount == 0) {
            val currentFormPage = currentFormInfo.form.displayedPage
            if (currentFormPage > 0) {
                setFormUseCase.setPagePosition(currentFormPage.dec())
            } else {
                confirmExit()
            }
        } else {
            formSingleLiveEvent.value = FormEvent.ExitFragment
        }
    }

    fun onCloseMenuItemClicked() {
        confirmExit()
    }

    private fun confirmExit() {
        if (currentFormInfo.hasModifications) {
            formSingleLiveEvent.value = FormEvent.ShowDialog(
                type = DialogType.EXIT,
                title = application.getString(R.string.exit_form_dialog_title),
                message = when (currentFormInfo.type) {
                    FormInfoEntity.FormType.ADD -> application.getString(R.string.exit_add_form_dialog_message)
                    FormInfoEntity.FormType.EDIT -> application.getString(R.string.exit_edit_form_dialog_message)
                },
                positiveButtonText = application.getString(R.string.exit_form_dialog_positive_button),
                negativeButtonText = application.getString(R.string.exit_form_dialog_negative_button)
            )
        } else {
            formSingleLiveEvent.value = FormEvent.ExitActivity
        }
    }

    fun onDialogPositiveButtonClicked(type: DialogType) {
        when (type) {
            DialogType.EXIT -> if (checkFormErrorUseCase.containsNoError(pageToCheck = 0)) {
                formSingleLiveEvent.value = FormEvent.ExitActivity
            }
            DialogType.DRAFT -> {}
        }
    }

    fun onDialogNegativeButtonClicked(type: DialogType) {
        setFormUseCase.reset()

        when (type) {
            DialogType.EXIT -> formSingleLiveEvent.value = FormEvent.ExitActivity
            DialogType.DRAFT -> setFormUseCase.initAddForm()
        }
    }

    fun onPhotoPicked(uri: Uri?, success: Boolean = true) {
        if (uri != null && success) {
            setFormUseCase.setPictureUri(uri)
        }
    }

    enum class DialogType {
        EXIT,
        DRAFT,
    }
}