package com.openclassrooms.realestatemanager.ui.filter

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.data.filter.FilterType
import com.openclassrooms.realestatemanager.data.filter.FilterValue

abstract class FilterViewModel<out T : FilterType, out V : FilterValue>(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    abstract fun onPositiveButtonClicked()
    abstract fun onNeutralButtonClicked()

    protected val filterType = savedStateHandle.get<T>(FilterDialog.KEY_FILTER_TYPE)
        ?: throw IllegalStateException("The filter type must be passed as an argument to the Fragment")
    protected val filterValue = savedStateHandle.get<V>(FilterDialog.KEY_FILTER_VALUE)
}