package com.openclassrooms.realestatemanager.ui.form

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.form.FormInfoEntity
import com.openclassrooms.realestatemanager.domain.displayed_picture.GetDisplayedPictureUseCase
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
    getDisplayedPictureUseCase: GetDisplayedPictureUseCase,
    private val application: Application,
) : ViewModel() {

    companion object {
        private const val EXIT_DIALOG = 0
        private const val DRAFT_DIALOG = 1
    }

    private val viewStateMediatorLiveData = MediatorLiveData<String>()
    val viewStateLiveData: LiveData<String> = viewStateMediatorLiveData
    private val formSingleLiveEvent = SingleLiveEvent<FormEvent>()
    val formEventLiveData: LiveData<FormEvent> = formSingleLiveEvent

    private val backStackEntryCountMutableLiveData = MutableLiveData<Int>()

    private lateinit var currentFormInfo: FormInfoEntity
    private var checkDraft = true
    private var whichDialog = -1

    init {
        val formInfoLiveData = getFormUseCase.getWithInfo()
        viewStateMediatorLiveData.addSource(formInfoLiveData) {
            combineViewState(it, backStackEntryCountMutableLiveData.value)
        }
        viewStateMediatorLiveData.addSource(backStackEntryCountMutableLiveData) {
            combineViewState(formInfoLiveData.value, it)
        }

        formSingleLiveEvent.addSource(getDisplayedPictureUseCase()) {
            if (it != null) {
                formSingleLiveEvent.value = FormEvent.ShowPicture
            }
        }

        formSingleLiveEvent.addSource(getFormUseCase.getExitFormRequest()) {
            if (it) {
                setFormUseCase.setExitRequest(false) // Reset flag
                formSingleLiveEvent.value = FormEvent.ExitActivity
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

    private fun checkExistingDraft(formInfo: FormInfoEntity) {
        if (checkDraft && formInfo.type == FormInfoEntity.FormType.ADD && formInfo.hasModifications) {
            whichDialog = DRAFT_DIALOG

            formSingleLiveEvent.value = FormEvent.ShowDialog(
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
            whichDialog = EXIT_DIALOG

            formSingleLiveEvent.value = FormEvent.ShowDialog(
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

    fun onDialogPositiveButtonClicked() {
        when (whichDialog) {
            EXIT_DIALOG -> {
                if (checkFormErrorUseCase.containsNoError(pageToCheck = 0)) {
                    formSingleLiveEvent.value = FormEvent.ExitActivity
                }
            }
            DRAFT_DIALOG -> {}
        }
    }

    fun onDialogNegativeButtonClicked() {
        setFormUseCase.reset()

        when (whichDialog) {
            EXIT_DIALOG -> formSingleLiveEvent.value = FormEvent.ExitActivity
            DRAFT_DIALOG -> setFormUseCase.initAddForm()
        }
    }
}