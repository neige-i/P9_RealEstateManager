package com.openclassrooms.realestatemanager.ui.filter.slider

import android.util.Range
import androidx.fragment.app.viewModels
import com.openclassrooms.realestatemanager.databinding.DialogSliderBinding
import com.openclassrooms.realestatemanager.ui.filter.FilterDialog
import com.openclassrooms.realestatemanager.ui.util.toCharSequence
import com.openclassrooms.realestatemanager.ui.util.viewBinding

class SliderFilterDialog private constructor() : FilterDialog() {

    override val binding by viewBinding(DialogSliderBinding::inflate)
    override val viewModel by viewModels<SliderFilterViewModel>()

    override fun initUi() {
        binding.dialogRangeSlider.addOnChangeListener { slider, _, _ ->
            viewModel.onSliderValuesChanged(Range(slider.values[0], slider.values[1]))
        }
    }

    override fun updateViewState() {
        viewModel.viewState.observe(this) { slider ->
            val sliderStyle = slider.style

            dialog?.setTitle(sliderStyle.dialogTitle)

            binding.dialogRangeTxt.text = sliderStyle.label.toCharSequence(requireContext())

            binding.dialogRangeSlider.valueFrom = slider.bounds.lower
            binding.dialogRangeSlider.valueTo = slider.bounds.upper

            binding.dialogRangeSlider.values = listOf(slider.selection.lower, slider.selection.upper)

            binding.dialogRangeSlider.stepSize = sliderStyle.step
        }
    }

    override fun triggerViewEvents() {}
}