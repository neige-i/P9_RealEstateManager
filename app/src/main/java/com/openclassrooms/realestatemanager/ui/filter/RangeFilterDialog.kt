package com.openclassrooms.realestatemanager.ui.filter

import android.app.Dialog
import android.util.Range
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.DialogSliderBinding
import com.openclassrooms.realestatemanager.ui.util.viewBinding

class RangeFilterDialog : FilterDialog() {

    private val binding by viewBinding(DialogSliderBinding::inflate)

    override fun getFilterDialog(): Dialog {
        val viewModel = ViewModelProvider(this).get(RangeFilterViewModel::class.java)

        binding.dialogRangeSlider.addOnChangeListener { slider, _, _ ->
            viewModel.onSliderValuesChanged(Range(slider.values[0], slider.values[1]))
        }
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("dummy non-empty text")
            .setPositiveButton(R.string.apply_filter) { _, _ -> viewModel.onPositiveButtonClicked() }
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

        viewModel.viewState.observe(this) { range ->
            dialog.setTitle(range.dialogTitle)

            binding.dialogRangeTxt.text = range.label

            binding.dialogRangeSlider.valueFrom = range.from
            binding.dialogRangeSlider.valueTo = range.to

            binding.dialogRangeSlider.values = listOf(range.min, range.max)

            binding.dialogRangeSlider.stepSize = range.step
        }

        return dialog
    }
}