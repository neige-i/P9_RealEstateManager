package com.openclassrooms.realestatemanager.ui.add_edit.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.openclassrooms.realestatemanager.databinding.FragmentSaleStatusBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SaleStatusFragment : Fragment() {

    private var mutableBinding: FragmentSaleStatusBinding? = null
    private val binding get() = mutableBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        mutableBinding = FragmentSaleStatusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel = ViewModelProvider(this).get(SaleStatusViewModel::class.java)

        binding.saleStatusAgentInput.setOnItemClickListener { parent, _, position, _ ->
            viewModel.onAgentSelected(parent.getItemAtPosition(position).toString())
        }
        binding.saleStatusSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onAvailabilitySwitched(isChecked)
        }

        binding.saleStatusMarketEntryDateInput.doAfterTextChanged {
            viewModel.onMarketEntryDateChanged(it?.toString())
        }
        binding.saleStatusSaleDateInput.doAfterTextChanged {
            viewModel.onSaleDateChanged(it?.toString())
        }

        viewModel.viewState.observe(viewLifecycleOwner) {
            binding.saleStatusAgentInput.setAdapter(ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                it.agentEntries
            ))

            binding.saleStatusAgentInput.setText(it.selectedAgentName, false)
            binding.saleStatusMarketEntryDateInput.setText(it.marketEntryDate)
            binding.saleStatusSaleDateInput.setText(it.saleDate)

            binding.saleStatusSwitch.isChecked = it.isAvailableForSale
            binding.saleStatusSaleDateInputLayout.isVisible = !it.isAvailableForSale
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mutableBinding = null
    }
}
