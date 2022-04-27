package com.openclassrooms.realestatemanager.ui.form

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.form.CurrentPictureRepository
import com.openclassrooms.realestatemanager.domain.form.CheckFormErrorUseCase
import com.openclassrooms.realestatemanager.domain.form.FormType
import com.openclassrooms.realestatemanager.domain.form.GetFormUseCase
import com.openclassrooms.realestatemanager.domain.form.SetFormUseCase
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
    currentPictureRepository: CurrentPictureRepository,
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

        viewModelScope.launch(coroutineProvider.getIoDispatcher()) {
            currentPictureRepository.getPictureFlow().collect {

                withContext(coroutineProvider.getMainDispatcher()) {
                    formSingleLiveEvent.value = FormEvent.ShowPicture
                }
            }
        }
    }

    private fun checkExistingDraft() {
        if (getFormUseCase.getFormInfo().formType == FormType.ADD_ESTATE &&
            getFormUseCase.getFormInfo().isModified
        ) {
            formSingleLiveEvent.value = FormEvent.ShowDialog(
                type = DialogType.SAVE_DRAFT,
                title = application.getString(R.string.draft_form_dialog_title),
                message = application.getString(
                    R.string.draft_form_dialog_message,
                    getFormUseCase.getFormInfo().estateType
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
            when (getFormUseCase.getFormInfo().formType) {
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
            saveRealEstateUseCase.invoke()
            setFormUseCase.reset()

            withContext(coroutineProvider.getMainDispatcher()) {
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
        if (getFormUseCase.getFormInfo().isModified) {
            formSingleLiveEvent.value = FormEvent.ShowDialog(
                type = DialogType.EXIT_FORM,
                title = application.getString(R.string.exit_form_dialog_title),
                message = when (getFormUseCase.getFormInfo().formType) {
                    FormType.ADD_ESTATE -> application.getString(R.string.exit_add_form_dialog_message)
                    FormType.EDIT_ESTATE -> application.getString(R.string.exit_edit_form_dialog_message)
                },
                positiveButtonText = application.getString(R.string.exit_form_dialog_positive_button),
                negativeButtonText = application.getString(R.string.exit_form_dialog_negative_button)
            )
        } else {
            setFormUseCase.reset()
            formSingleLiveEvent.value = FormEvent.ExitActivity
        }
    }

    fun onDialogPositiveButtonClicked(type: DialogType) {
        when (type) {
            DialogType.EXIT_FORM -> if (checkFormErrorUseCase.containsNoError(pageToCheck = 0)) {
                formSingleLiveEvent.value = FormEvent.ExitActivity
            }
            DialogType.SAVE_DRAFT -> {}
        }
    }

    fun onDialogNegativeButtonClicked(type: DialogType) {
        setFormUseCase.reset()

        when (type) {
            DialogType.EXIT_FORM -> formSingleLiveEvent.value = FormEvent.ExitActivity
            DialogType.SAVE_DRAFT -> {}
        }
    }

    private fun isLastPageDisplayed(): Boolean = currentPage == pageCount - 1

    enum class DialogType {
        EXIT_FORM,
        SAVE_DRAFT,
    }
}