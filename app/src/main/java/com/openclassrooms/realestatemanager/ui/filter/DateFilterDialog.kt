package com.openclassrooms.realestatemanager.ui.filter

import android.app.Dialog
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.filter.FilterValue
import com.openclassrooms.realestatemanager.databinding.DialogSaleStatusFilterBinding
import com.openclassrooms.realestatemanager.ui.filter.DateFilterViewModel.DatePickerType
import com.openclassrooms.realestatemanager.ui.util.viewBinding

class DateFilterDialog private constructor() : FilterDialog() {

    companion object {
        const val KEY_FILTER_VALUE = "KEY_FILTER_VALUE"

        fun newInstance(availableDatesFilter: FilterValue.AvailableDates?) = DateFilterDialog().apply {
            arguments = Bundle().apply {
                putSerializable(KEY_FILTER_VALUE, availableDatesFilter)
            }
        }
    }

    private val binding by viewBinding(DialogSaleStatusFilterBinding::inflate)

    override fun getFilterDialog(): Dialog {
        val viewModel = ViewModelProvider(this).get(DateFilterViewModel::class.java)

        binding.filterAvailableEstatesRadioBtn.setOnCheckedChangeListener { _, _ -> viewModel.onSaleStatusSelected(true) }
        binding.filterSoldEstatesRadioBtn.setOnCheckedChangeListener { _, _ -> viewModel.onSaleStatusSelected(false) }

        binding.filterStartDateInput.setOnClickListener {
            viewModel.onDateInputClicked(DatePickerType.START)
        }
        binding.filterStartDateLyt.setEndIconOnClickListener {
            viewModel.onDateInputCleared(DatePickerType.START)
        }

        binding.filterEndDateInput.setOnClickListener {
            viewModel.onDateInputClicked(DatePickerType.END)
        }
        binding.filterEndDateLyt.setEndIconOnClickListener {
            viewModel.onDateInputCleared(DatePickerType.END)

        }

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.filter_sale_dialog_title)
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

        viewModel.viewState.observe(this) { dateFilter ->
            binding.filterSaleRadioGrp.check(dateFilter.selectedRadioBtn)

            binding.filterStartDateLyt.isVisible = dateFilter.isDateInputVisible
            binding.filterStartDateLyt.isEndIconVisible = dateFilter.isStartDateEndIconVisible
            binding.filterStartDateInput.setText(dateFilter.startDateInputText)

            binding.filterEndDateLyt.isVisible = dateFilter.isDateInputVisible
            binding.filterEndDateLyt.isEndIconVisible = dateFilter.isEndDateEndIconVisible
            binding.filterEndDateInput.setText(dateFilter.endDateInputText)
        }

        viewModel.showDatePickerEvent.observe(this) { showDatePicker ->
            val calendarConstraintsBuilder = CalendarConstraints.Builder()
                .setValidator(
                    DateValidatorBounds(
                        from = showDatePicker.minConstraint,
                        until = showDatePicker.maxConstraint,
                    )
                )
            showDatePicker.minConstraint?.let { min -> calendarConstraintsBuilder.setStart(min) }
            showDatePicker.maxConstraint?.let { max -> calendarConstraintsBuilder.setEnd(max) }

            val datePicker = MaterialDatePicker.Builder
                .datePicker()
                .setSelection(showDatePicker.selectedDate)
                .setCalendarConstraints(calendarConstraintsBuilder.build())
                .build()

            datePicker.addOnPositiveButtonClickListener { dateMillis ->
                showDatePicker.onValidated(dateMillis)
            }
            datePicker.show(parentFragmentManager, null)
        }

        return dialog
    }
}