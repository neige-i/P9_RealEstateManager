package com.openclassrooms.realestatemanager.ui.filter

import android.app.Dialog
import android.os.Bundle
import android.util.Range
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.filter.FilterType
import com.openclassrooms.realestatemanager.data.filter.FilterValue
import com.openclassrooms.realestatemanager.databinding.DialogSliderBinding
import com.openclassrooms.realestatemanager.ui.util.viewBinding

class RangeFilterDialog private constructor() : FilterDialog() {

    companion object {
        const val KEY_FILTER_TYPE = "KEY_FILTER_TYPE"
        const val KEY_FILTER_VALUE = "KEY_FILTER_VALUE"

        fun newInstance(sliderFilter: FilterType.Slider, minMaxFilter: FilterValue.MinMax<*>?) = RangeFilterDialog().apply {
            arguments = Bundle().apply {
                putSerializable(KEY_FILTER_TYPE, sliderFilter)
                putSerializable(KEY_FILTER_VALUE, minMaxFilter)
            }
        }
    }

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

        viewModel.viewState.observe(this) { rangeDialog ->
            val dialogStyle = rangeDialog.style

            dialog.setTitle(dialogStyle.title)

            binding.dialogRangeTxt.text = dialogStyle.label

            binding.dialogRangeSlider.valueFrom = rangeDialog.bounds.lower
            binding.dialogRangeSlider.valueTo = rangeDialog.bounds.upper

            binding.dialogRangeSlider.values = listOf(
                rangeDialog.selection.lower,
                rangeDialog.selection.upper
            )

            binding.dialogRangeSlider.stepSize = dialogStyle.step
        }

        return dialog
    }
}