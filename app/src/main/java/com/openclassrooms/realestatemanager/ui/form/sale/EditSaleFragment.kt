package com.openclassrooms.realestatemanager.ui.form.sale

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.openclassrooms.realestatemanager.databinding.FragmentEditSaleBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditSaleFragment : Fragment() {

    private var mutableBinding: FragmentEditSaleBinding? = null
    private val binding get() = mutableBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        mutableBinding = FragmentEditSaleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel = ViewModelProvider(this).get(EditSaleViewModel::class.java)

        binding.saleAgentInput.setOnItemClickListener { parent, _, position, _ ->
            viewModel.onAgentSelected(parent.getItemAtPosition(position).toString())
        }
        binding.saleSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onAvailabilitySwitched(isChecked)
        }

        binding.saleMarketEntryDateInput.doAfterTextChanged {
            viewModel.onMarketEntryDateChanged(it?.toString())
        }
        binding.saleDateInput.doAfterTextChanged {
            viewModel.onSaleDateChanged(it?.toString())
        }

        viewModel.viewStateLiveData.observe(viewLifecycleOwner) {
            binding.saleAgentInput.setAdapter(ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                it.agentEntries
            ))

            binding.saleAgentInput.setText(it.selectedAgentName, false)
            binding.saleMarketEntryDateInput.setText(it.marketEntryDate)
            binding.saleDateInput.setText(it.saleDate)

            binding.saleSwitch.isChecked = it.isAvailableForSale
            binding.saleDateInputLayout.isVisible = !it.isAvailableForSale

            binding.saleDateInputLayout.error = it.saleDateError
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mutableBinding = null
    }
}
