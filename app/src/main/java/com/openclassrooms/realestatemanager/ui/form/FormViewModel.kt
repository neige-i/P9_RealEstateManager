package com.openclassrooms.realestatemanager.ui.form

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.form.FormInfoEntity
import com.openclassrooms.realestatemanager.domain.form.CheckFormErrorUseCase
import com.openclassrooms.realestatemanager.domain.form.GetFormUseCase
import com.openclassrooms.realestatemanager.domain.form.SetFormUseCase
import com.openclassrooms.realestatemanager.domain.real_estate.CreateRealEstateUseCase
import com.openclassrooms.realestatemanager.ui.CoroutineProvider
import com.openclassrooms.realestatemanager.ui.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FormViewModel @Inject constructor(
    getFormUseCase: GetFormUseCase,
    private val checkFormErrorUseCase: CheckFormErrorUseCase,
    private val setFormUseCase: SetFormUseCase,
    private val createRealEstateUseCase: CreateRealEstateUseCase,
    private val coroutineProvider: CoroutineProvider,
    private val application: Application,
) : ViewModel() {

    private val viewStateMutableLiveData = MutableLiveData<FormViewState>()
    val viewStateLiveData: LiveData<FormViewState> = viewStateMutableLiveData
    private val formSingleLiveEvent = SingleLiveEvent<FormEvent>()
    val formEventLiveData: LiveData<FormEvent> = formSingleLiveEvent

    private lateinit var currentFormInfo: FormInfoEntity
    private var checkDraft = true
    private var pageCount = -1
    private var currentPage = -1

    init {
        formSingleLiveEvent.addSource(getFormUseCase.getWithInfo()) {
            currentFormInfo = it

            if (checkDraft) {
                // Reset flag to display draft dialog only once: at ViewModel initialization
                checkDraft = false

                checkExistingDraft(it)
            }
        }

        formSingleLiveEvent.addSource(getFormUseCase.getDisplayedPicture()) {
            it?.let {
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

    private fun checkExistingDraft(formInfo: FormInfoEntity) {
        if (formInfo.type == FormInfoEntity.FormType.ADD && formInfo.hasModifications) {
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

    fun onSubmitButtonClicked() {
        if (checkFormErrorUseCase.containsNoError(pageToCheck = currentPage)) {
            if (isLastPageDisplayed()) {
                onFormCompleted()
            } else {
                formSingleLiveEvent.value = FormEvent.GoToPage(currentPage + 1)
            }
        }
    }

    private fun onFormCompleted() {
        viewModelScope.launch(coroutineProvider.getIoDispatcher()) {
            createRealEstateUseCase()
            setFormUseCase.reset()
            setFormUseCase.setExitRequest(true)
        }
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

    private fun isLastPageDisplayed(): Boolean = currentPage == pageCount - 1

    enum class DialogType {
        EXIT,
        DRAFT,
    }
}