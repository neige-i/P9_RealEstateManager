package com.openclassrooms.realestatemanager.ui.filter.date

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.openclassrooms.realestatemanager.data.filter.FilterType
import com.openclassrooms.realestatemanager.data.filter.FilterValue
import com.openclassrooms.realestatemanager.databinding.DialogSaleStatusFilterBinding
import com.openclassrooms.realestatemanager.ui.filter.FilterDialog
import com.openclassrooms.realestatemanager.ui.filter.date.DateFilterViewModel.DatePickerType
import com.openclassrooms.realestatemanager.ui.util.viewBinding

class DateFilterDialog private constructor() : FilterDialog() {

    companion object {
        fun newInstance(filterType: FilterType, filterValue: FilterValue?) = DateFilterDialog().createInstance(filterType, filterValue)
    }

    override val binding by viewBinding(DialogSaleStatusFilterBinding::inflate)
    override val viewModel by viewModels<DateFilterViewModel>()

    override fun initUi() {
        binding.filterAvailableEstatesRadioBtn.setOnCheckedChangeListener { _, _ -> viewModel.onSaleStatusSelected(true) }
        binding.filterSoldEstatesRadioBtn.setOnCheckedChangeListener { _, _ -> viewModel.onSaleStatusSelected(false) }

        binding.filterStartDateInput.setOnClickListener { viewModel.onDateInputClicked(DatePickerType.START) }
        binding.filterStartDateLyt.setEndIconOnClickListener { viewModel.onDateInputCleared(DatePickerType.START) }

        binding.filterEndDateInput.setOnClickListener { viewModel.onDateInputClicked(DatePickerType.END) }
        binding.filterEndDateLyt.setEndIconOnClickListener { viewModel.onDateInputCleared(DatePickerType.END) }
    }

    override fun updateViewState() {
        viewModel.viewState.observe(this) { dateFilter ->
            dialog?.setTitle(dateFilter.dialogTitle)

            binding.filterSaleRadioGrp.check(dateFilter.selectedRadioBtn)

            binding.filterStartDateLyt.isVisible = dateFilter.isDateInputVisible
            binding.filterStartDateLyt.isEndIconVisible = dateFilter.isStartDateEndIconVisible
            binding.filterStartDateInput.setText(dateFilter.startDateInputText)

            binding.filterEndDateLyt.isVisible = dateFilter.isDateInputVisible
            binding.filterEndDateLyt.isEndIconVisible = dateFilter.isEndDateEndIconVisible
            binding.filterEndDateInput.setText(dateFilter.endDateInputText)
        }
    }

    override fun triggerViewEvents() {
        viewModel.viewEvent.observe(this) { showDatePicker ->
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
    }
}