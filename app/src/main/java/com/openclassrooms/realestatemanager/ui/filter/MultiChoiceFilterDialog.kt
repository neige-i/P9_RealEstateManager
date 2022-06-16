package com.openclassrooms.realestatemanager.ui.filter

import android.app.Dialog
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.filter.FilterType
import com.openclassrooms.realestatemanager.data.filter.FilterValue
import com.openclassrooms.realestatemanager.databinding.FragmentListBinding
import com.openclassrooms.realestatemanager.ui.util.viewBinding

class MultiChoiceFilterDialog private constructor() : FilterDialog() {

    companion object {
        const val KEY_FILTER_TYPE = "KEY_FILTER_TYPE"
        const val KEY_FILTER_VALUE = "KEY_FILTER_VALUE"

        fun newInstance(checkListFilter: FilterType.CheckList, choicesFilter: FilterValue.Choices?) = MultiChoiceFilterDialog().apply {
            arguments = Bundle().apply {
                putSerializable(KEY_FILTER_TYPE, checkListFilter)
                putSerializable(KEY_FILTER_VALUE, choicesFilter)
            }
        }
    }

    private val binding by viewBinding(FragmentListBinding::inflate)

    override fun getFilterDialog(): Dialog {
        val viewModel = ViewModelProvider(this).get(MultiChoiceFilterViewModel::class.java)

        val checkListAdapter = CheckListAdapter()
        binding.root.adapter = checkListAdapter

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(" ")
            .setPositiveButton(R.string.apply_filter) { dialog, which -> viewModel.onPositiveButtonClicked() }
            .setNegativeButton(R.string.cancel_filter, null)
            .setNeutralButton(R.string.reset_filter, null) // Behavior overridden in OnShowListener
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

        viewModel.viewState.observe(this) { multiChoice ->
            dialog.setTitle(multiChoice.dialogTitle)
            checkListAdapter.submitList(multiChoice.checkItems)
        }

        return dialog
    }
}