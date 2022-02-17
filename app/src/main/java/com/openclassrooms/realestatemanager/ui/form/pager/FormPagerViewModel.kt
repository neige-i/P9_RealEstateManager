package com.openclassrooms.realestatemanager.ui.form.pager

import android.app.Application
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.domain.form.CheckFormErrorUseCase
import com.openclassrooms.realestatemanager.domain.form.GetFormUseCase
import com.openclassrooms.realestatemanager.domain.form.SetFormUseCase
import com.openclassrooms.realestatemanager.domain.real_estate.CreateRealEstateUseCase
import com.openclassrooms.realestatemanager.ui.CoroutineProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FormPagerViewModel @Inject constructor(
    getFormUseCase: GetFormUseCase,
    private val checkFormErrorUseCase: CheckFormErrorUseCase,
    private val setFormUseCase: SetFormUseCase,
    private val createRealEstateUseCase: CreateRealEstateUseCase,
    private val coroutineProvider: CoroutineProvider,
    private val application: Application,
) : ViewModel() {

    val viewStateLiveData = Transformations.map(getFormUseCase.getUpdates()) {
        currentPage = it.displayedPage

        FormPagerViewState(
            toolbarTitle = if (isLastPageDisplayed()) {
                application.getString(R.string.button_text_save)
            } else {
                application.getString(R.string.button_text_next)
            },
            pagerPosition = currentPage
        )
    }

    private var pageCount = -1
    private var currentPage = -1

    fun onInitPagerAdapter(pageCount: Int) {
        this.pageCount = pageCount
        setFormUseCase.setPageCount(pageCount)
    }

    fun onSubmitButtonClicked() {
        if (checkFormErrorUseCase.containsNoError(pageToCheck = currentPage)) {
            if (isLastPageDisplayed()) {
                onFormCompleted()
            } else {
                setFormUseCase.setPagePosition(currentPage + 1)
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

    private fun isLastPageDisplayed(): Boolean = currentPage == pageCount - 1
}