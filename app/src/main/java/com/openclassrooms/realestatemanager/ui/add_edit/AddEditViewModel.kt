package com.openclassrooms.realestatemanager.ui.add_edit

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.ui.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddEditViewModel @Inject constructor(
    private val application: Application,
) : ViewModel() {

    private val viewStateMutableLiveData = MutableLiveData<AddEditViewState>()
    val viewStateLiveData: LiveData<AddEditViewState> = viewStateMutableLiveData
    private val addEditEventSingleLiveEvent = SingleLiveEvent<AddEditEvent>()
    val addEditEventLiveData: LiveData<AddEditEvent> = addEditEventSingleLiveEvent

    private var pageCount = 0
    private var currentPage = 0

    fun onInitPagerAdapter(pageCount: Int) {
        this.pageCount = pageCount
    }

    fun onPageChanged(position: Int) {
        currentPage = position

        viewStateMutableLiveData.value = AddEditViewState(
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
        addEditEventSingleLiveEvent.value = if (currentPage > 0) {
            AddEditEvent.GoToPage(currentPage - 1)
        } else {
            AddEditEvent.ExitActivity
        }
    }

    fun onSubmitButtonClicked() {
        // Save to repo

        if (isLastPageDisplayed()) {
            addEditEventSingleLiveEvent.value = AddEditEvent.ExitActivity
        } else {
            addEditEventSingleLiveEvent.value = AddEditEvent.GoToPage(currentPage + 1)
        }
    }

    private fun isLastPageDisplayed(): Boolean = currentPage == pageCount - 1
}