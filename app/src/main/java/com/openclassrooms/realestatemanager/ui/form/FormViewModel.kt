package com.openclassrooms.realestatemanager.ui.form

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.domain.form.CheckFormErrorUseCase
import com.openclassrooms.realestatemanager.ui.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FormViewModel @Inject constructor(
    private val checkFormErrorUseCase: CheckFormErrorUseCase,
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
        formEventSingleLiveEvent.value = if (currentPage > 0) {
            FormEvent.GoToPage(currentPage - 1)
        } else {
            FormEvent.ExitActivity
        }
    }

    fun onCloseMenuItemClicked() {
        formEventSingleLiveEvent.value = FormEvent.ExitActivity
    }

    fun onSubmitButtonClicked() {
        if (checkFormErrorUseCase.containsNoError(currentPage)) {

            if (isLastPageDisplayed()) {
                formEventSingleLiveEvent.value = FormEvent.ExitActivity
            } else {
                formEventSingleLiveEvent.value = FormEvent.GoToPage(currentPage + 1)
            }
        }
    }

    private fun isLastPageDisplayed(): Boolean = currentPage == pageCount - 1
}