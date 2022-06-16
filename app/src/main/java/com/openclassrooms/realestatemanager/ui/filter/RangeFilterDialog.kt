package com.openclassrooms.realestatemanager.ui.filter

import android.util.Range
import androidx.fragment.app.viewModels
import com.openclassrooms.realestatemanager.data.filter.FilterType
import com.openclassrooms.realestatemanager.data.filter.FilterValue
import com.openclassrooms.realestatemanager.databinding.DialogSliderBinding
import com.openclassrooms.realestatemanager.ui.util.viewBinding

class RangeFilterDialog private constructor() : FilterDialog() {

    companion object {
        fun newInstance(filterType: FilterType, filterValue: FilterValue?) = RangeFilterDialog().createInstance(filterType, filterValue)
    }

    override val binding by viewBinding(DialogSliderBinding::inflate)
    override val viewModel by viewModels<RangeFilterViewModel>()

    override fun initUi() {
        binding.dialogRangeSlider.addOnChangeListener { slider, _, _ ->
            viewModel.onSliderValuesChanged(Range(slider.values[0], slider.values[1]))
        }
    }

    override fun updateViewState() {
        viewModel.viewState.observe(this) { rangeDialog ->
            val dialogStyle = rangeDialog.style

            dialog?.setTitle(dialogStyle.title)

            binding.dialogRangeTxt.text = dialogStyle.label

            binding.dialogRangeSlider.valueFrom = rangeDialog.bounds.lower
            binding.dialogRangeSlider.valueTo = rangeDialog.bounds.upper

            binding.dialogRangeSlider.values = listOf(
                rangeDialog.selection.lower,
                rangeDialog.selection.upper
            )

            binding.dialogRangeSlider.stepSize = dialogStyle.step
        }
    }

    override fun triggerViewEvents() {
    }
}