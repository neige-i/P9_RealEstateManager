package com.openclassrooms.realestatemanager.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.util.SingleLiveEvent

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val mutableViewState = MutableLiveData<MainViewState>()
    val viewState: LiveData<MainViewState> = mutableViewState

    private val mainMutableEvent = SingleLiveEvent<MainEvent>()
    val mainEvent: LiveData<MainEvent> = mainMutableEvent

    private var detailContainerId: Int? = null
    private var isTabletSize: Boolean? = null

    fun onActivityCreated(mainContentId: Int?, rightContentId: Int?) {
        detailContainerId = mainContentId ?: rightContentId
        isTabletSize = rightContentId != null
    }

    /**
     * Sets the fragment transaction info.
     */
    fun onShowDetails(item: String) {
        mainMutableEvent.value = detailContainerId?.let {
            ShowDetailEvent(
                containerId = it,
                arg = item
            )
        }
    }

    /**
     * Updates the Toolbar title and up button.
     */
    fun onBackStackChanged(backStackEntryCount: Int) {
        val showMainTitle = isTabletSize == true || backStackEntryCount == 0

        mutableViewState.value = MainViewState(
            title = if (showMainTitle) {
                getApplication<Application>().getString(R.string.app_name)
            } else {
                "Detail"
            },
            isHomeAsUpEnabled = !showMainTitle
        )
    }

    /**
     * Decides whether to close the activity or to let the default behaviour happen.
     */
    fun onBackPressed() {
        if (isTabletSize == true) {
            mainMutableEvent.value = EndActivityEvent
        } else {
            mainMutableEvent.value = GoBackEvent
        }
    }
}