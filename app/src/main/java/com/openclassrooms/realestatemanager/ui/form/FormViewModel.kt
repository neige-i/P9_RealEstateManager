package com.openclassrooms.realestatemanager.ui.form

import android.app.Application
import androidx.lifecycle.*
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.form.ActionRepository
import com.openclassrooms.realestatemanager.domain.form.*
import com.openclassrooms.realestatemanager.domain.real_estate.SaveRealEstateUseCase
import com.openclassrooms.realestatemanager.ui.util.CoroutineProvider
import com.openclassrooms.realestatemanager.ui.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FormViewModel @Inject constructor(
    private val getFormUseCase: GetFormUseCase,
    actionRepository: ActionRepository,
    private val checkFormErrorUseCase: CheckFormErrorUseCase,
    private val setFormUseCase: SetFormUseCase,
    private val saveRealEstateUseCase: SaveRealEstateUseCase,
    private val coroutineProvider: CoroutineProvider,
    private val application: Application,
) : ViewModel() {

    private val viewStateMutableLiveData = MutableLiveData<FormViewState>()
    val viewStateLiveData: LiveData<FormViewState> = viewStateMutableLiveData
    private val formSingleLiveEvent = SingleLiveEvent<FormEvent>()
    val formEventLiveData: LiveData<FormEvent> = formSingleLiveEvent

    private var pageCount = -1
    private var currentPage = -1

    init {
        checkExistingDraft()

        formSingleLiveEvent.addSource(
            actionRepository.getPictureOpenerFlow().asLiveData(coroutineProvider.getIoDispatcher())
        ) {
            formSingleLiveEvent.value = FormEvent.ShowPicture
        }
    }

    private fun getFormInfo(): FormInfo = getFormUseCase.getFormInfo()

    private fun checkExistingDraft() {
        if (getFormInfo().formType == FormType.ADD_ESTATE && getFormInfo().isModified) {

            formSingleLiveEvent.value = FormEvent.ShowDialog(
                type = DialogType.DRAFT,
                title = application.getString(R.string.draft_form_dialog_title),
                message = application.getString(
                    R.string.draft_form_dialog_message,
                    getFormInfo().estateType
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

        val addOrEdit = application.getString(
            when (getFormInfo().formType) {
                FormType.ADD_ESTATE -> R.string.toolbar_title_add
                FormType.EDIT_ESTATE -> R.string.toolbar_title_edit
            }
        )

        viewStateMutableLiveData.value = FormViewState(
            toolbarTitle = "$addOrEdit (${currentPage + 1}/$pageCount)",
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
            saveRealEstateUseCase()

            withContext(coroutineProvider.getMainDispatcher()) {
                setFormUseCase.reset()
                formSingleLiveEvent.value = FormEvent.ExitActivity
            }
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
        if (getFormInfo().isModified) {
            formSingleLiveEvent.value = FormEvent.ShowDialog(
                type = DialogType.EXIT,
                title = application.getString(R.string.exit_form_dialog_title),
                message = when (getFormInfo().formType) {
                    FormType.ADD_ESTATE -> application.getString(R.string.exit_add_form_dialog_message)
                    FormType.EDIT_ESTATE -> application.getString(R.string.exit_edit_form_dialog_message)
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
            DialogType.DRAFT -> {}
        }
    }

    private fun isLastPageDisplayed(): Boolean = currentPage == pageCount - 1

    enum class DialogType {
        EXIT,
        DRAFT,
    }
}