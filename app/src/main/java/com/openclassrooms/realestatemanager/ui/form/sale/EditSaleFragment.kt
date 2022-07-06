package com.openclassrooms.realestatemanager.ui.form.sale

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.datepicker.MaterialDatePicker
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.room.AgentEntity
import com.openclassrooms.realestatemanager.databinding.FragmentEditSaleBinding
import com.openclassrooms.realestatemanager.ui.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditSaleFragment : Fragment(R.layout.fragment_edit_sale) {

    private val binding by viewBinding { FragmentEditSaleBinding.bind(it) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel = ViewModelProvider(this).get(EditSaleViewModel::class.java)

        binding.saleAgentInput.setOnItemClickListener { parent, _, position, _ ->
            viewModel.onAgentSelected(parent.getItemAtPosition(position) as AgentEntity)
        }
        binding.saleSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onAvailabilitySwitched(isChecked)
        }

        binding.saleMarketEntryDateInput.setOnClickListener { viewModel.onMarketEntryDateClicked() }
        binding.saleDateInput.setOnClickListener { viewModel.onSaleDateClicked() }

        viewModel.viewStateLiveData.observe(viewLifecycleOwner) {
            binding.saleAgentInput.setAdapter(
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    it.allAgents
                )
            )

            binding.saleAgentInput.setText(it.selectedAgentName, false)
            binding.saleMarketEntryDateInput.setText(it.marketEntryDate)
            binding.saleDateInput.setText(it.saleDate)

            binding.saleSwitch.isChecked = it.isAvailableForSale
            binding.saleDateInputLayout.isVisible = !it.isAvailableForSale

            binding.saleMarketEntryDateInputLayout.error = it.marketEntryDateError
            binding.saleDateInputLayout.error = it.saleDateError
        }

        viewModel.showDatePickerEventLiveData.observe(viewLifecycleOwner) { showDatePickerEvent ->
            val datePicker = MaterialDatePicker.Builder
                .datePicker()
                .setTitleText(showDatePickerEvent.title)
                .setSelection(showDatePickerEvent.dateMillis)
                .build()

            datePicker.addOnPositiveButtonClickListener { dateMillis ->
                viewModel.onDateSelected(dateMillis, showDatePickerEvent.type)
            }
            datePicker.show(parentFragmentManager, null)
        }
    }
}
