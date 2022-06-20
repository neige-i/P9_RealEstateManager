package com.openclassrooms.realestatemanager.ui.filter

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.filter.FilterType
import com.openclassrooms.realestatemanager.data.filter.FilterValue
import dagger.hilt.android.AndroidEntryPoint
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

@AndroidEntryPoint
abstract class FilterDialog : DialogFragment() {

    companion object {
        const val KEY_FILTER_TYPE = "KEY_FILTER_TYPE"
        const val KEY_FILTER_VALUE = "KEY_FILTER_VALUE"

        fun <FD : FilterDialog> newInstance(filterKlass: KClass<FD>, filterType: FilterType, filterValue: FilterValue?): FD =
            filterKlass.createInstance().apply {
                arguments = Bundle().apply {
                    putSerializable(KEY_FILTER_TYPE, filterType)
                    putSerializable(KEY_FILTER_VALUE, filterValue)
                }
            }
    }

    protected abstract val binding: ViewBinding
    protected abstract val viewModel: FilterViewModel<FilterType, FilterValue>

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        initUi()

        val dialog = MaterialAlertDialogBuilder(requireContext())
            // Dummy non-empty text, otherwise, title won't be shown if set after the dialog being displayed
            .setTitle(" ")
            .setPositiveButton(R.string.apply_filter) { _, _ -> viewModel.onPositiveButtonClicked() }
            .setNegativeButton(R.string.cancel_filter, null)
            // Behavior overridden in OnShowListener, to prevent dialog from dismissing when clicking on the button
            .setNeutralButton(R.string.reset_filter, null)
            .setView(binding.root)
            .create()
            .apply {
                setOnShowListener {
                    // getButton() return null if dialog is not shown yet
                    getButton(Dialog.BUTTON_NEUTRAL).setOnClickListener {
                        viewModel.onNeutralButtonClicked()
                    }
                }
            }

        updateViewState()
        triggerViewEvents()

        return dialog
    }

    abstract fun initUi()

    abstract fun updateViewState()

    abstract fun triggerViewEvents()

}