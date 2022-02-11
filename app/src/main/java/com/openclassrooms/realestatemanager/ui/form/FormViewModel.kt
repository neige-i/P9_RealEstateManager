package com.openclassrooms.realestatemanager.ui.form

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.form.FormRepository
import com.openclassrooms.realestatemanager.domain.form.CheckFormErrorUseCase
import com.openclassrooms.realestatemanager.domain.form.GetFormUseCase
import com.openclassrooms.realestatemanager.domain.form.SetFormUseCase
import com.openclassrooms.realestatemanager.ui.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FormViewModel @Inject constructor(
    private val getFormInfoUseCase: GetFormUseCase,
    private val checkFormErrorUseCase: CheckFormErrorUseCase,
    private val editFormUseCase: SetFormUseCase,
    private val application: Application,
) : ViewModel() {

    companion object {
        private const val EXIT_DIALOG = 0
        private const val DRAFT_DIALOG = 1
    }

    private val viewStateMutableLiveData = MutableLiveData<FormViewState>()
    val viewStateLiveData: LiveData<FormViewState> = viewStateMutableLiveData
    private val formSingleLiveEvent = SingleLiveEvent<FormEvent>()
    val formEventLiveData: LiveData<FormEvent> = formSingleLiveEvent

    private var pageCount = 0
    private var currentPage = 0
    private var whichDialog = -1

    init {
        if (getFormInfoUseCase.getType() == FormRepository.FormType.ADD &&
            getFormInfoUseCase.isModified()
        ) {
            whichDialog = DRAFT_DIALOG

            formSingleLiveEvent.value = FormEvent.ShowDialog(
                title = application.getString(R.string.draft_form_dialog_title),
                message = application.getString(
                    R.string.draft_form_dialog_message,
                    getFormInfoUseCase.getCurrent().type
                ),
                positiveButtonText = application.getString(R.string.draft_form_dialog_positive_button),
                negativeButtonText = application.getString(R.string.draft_form_dialog_negative_button)
            )
        }
    }

    fun onInitPagerAdapter(pageCount: Int) {
        this.pageCount = pageCount
    }

    fun onPageChanged(position: Int) {
        currentPage = position

        viewStateMutableLiveData.value = FormViewState(
            toolbarTitle = application.getString(R.string.toolbar_title_add) +
                    " (" + (currentPage + 1) +
                    "/" + pageCount +
                    ")",
            submitButtonText = if (isLastPageDisplayed()) {
                application.getString(R.string.button_text_save)
            } else {
                application.getString(R.string.button_text_next)
            }
        )
    }

    fun onGoBack() {
        if (currentPage > 0) {
            formSingleLiveEvent.value = FormEvent.GoToPage(currentPage - 1)
        } else {
            confirmExit()
        }
    }

    fun onCloseMenuItemClicked() {
        confirmExit()
    }

    private fun confirmExit() {
        if (getFormInfoUseCase.isModified()) {
            whichDialog = EXIT_DIALOG

            formSingleLiveEvent.value = FormEvent.ShowDialog(
                title = application.getString(R.string.exit_form_dialog_title),
                message = when (getFormInfoUseCase.getType()) {
                    FormRepository.FormType.ADD -> application.getString(R.string.exit_add_form_dialog_message)
                    FormRepository.FormType.EDIT -> application.getString(R.string.exit_edit_form_dialog_message)
                },
                positiveButtonText = application.getString(R.string.exit_form_dialog_positive_button),
                negativeButtonText = application.getString(R.string.exit_form_dialog_negative_button)
            )
        } else {
            formSingleLiveEvent.value = FormEvent.ExitActivity
        }
    }

    fun onSubmitButtonClicked() {
        if (checkFormErrorUseCase.containsNoError(pageToCheck = currentPage)) {
            if (isLastPageDisplayed()) {
                resetFormAndExit()
            } else {
                formSingleLiveEvent.value = FormEvent.GoToPage(currentPage + 1)
            }
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
        when (whichDialog) {
            EXIT_DIALOG -> resetFormAndExit()
            DRAFT_DIALOG -> {
                editFormUseCase.reset()
                editFormUseCase.initAddForm()
            }
        }
    }

    private fun resetFormAndExit() {
        editFormUseCase.reset()
        formSingleLiveEvent.value = FormEvent.ExitActivity
    }

    private fun isLastPageDisplayed(): Boolean = currentPage == pageCount - 1
}