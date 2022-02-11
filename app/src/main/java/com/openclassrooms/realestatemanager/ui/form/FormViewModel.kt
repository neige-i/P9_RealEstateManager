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

    private val viewStateMutableLiveData = MutableLiveData<FormViewState>()
    val viewStateLiveData: LiveData<FormViewState> = viewStateMutableLiveData
    private val formEventSingleLiveEvent = SingleLiveEvent<FormEvent>()
    val formEventLiveData: LiveData<FormEvent> = formEventSingleLiveEvent

    private var pageCount = 0
    private var currentPage = 0

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
            formEventSingleLiveEvent.value = FormEvent.GoToPage(currentPage - 1)
        } else {
            confirmExit()
        }
    }

    fun onCloseMenuItemClicked() {
        confirmExit()
    }

    private fun confirmExit() {
        if (getFormInfoUseCase.isModified()) {

            formEventSingleLiveEvent.value = FormEvent.ShowExitDialog(
                dialogMessage = when (getFormInfoUseCase.getType()) {
                    FormRepository.FormType.ADD -> application.getString(R.string.exit_add_form_dialog_message)
                    FormRepository.FormType.EDIT -> application.getString(R.string.exit_edit_form_dialog_message)
                }
            )
        } else {
            formEventSingleLiveEvent.value = FormEvent.ExitActivity
        }
    }

    fun onSubmitButtonClicked() {
        if (checkFormErrorUseCase.containsNoError(pageToCheck = currentPage)) {
            if (isLastPageDisplayed()) {
                resetFormAndExit()
            } else {
                formEventSingleLiveEvent.value = FormEvent.GoToPage(currentPage + 1)
            }
        }
    }

    fun onDialogPositiveButtonClicked() {
        if (checkFormErrorUseCase.containsNoError(pageToCheck = 0)) {
            formEventSingleLiveEvent.value = FormEvent.ExitActivity
        }
    }

    fun onDialogNegativeButtonClicked() {
        resetFormAndExit()
    }

    private fun resetFormAndExit() {
        editFormUseCase.reset()
        formEventSingleLiveEvent.value = FormEvent.ExitActivity
    }

    private fun isLastPageDisplayed(): Boolean = currentPage == pageCount - 1
}