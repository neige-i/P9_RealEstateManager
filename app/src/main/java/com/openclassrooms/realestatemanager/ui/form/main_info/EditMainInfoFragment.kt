package com.openclassrooms.realestatemanager.ui.form.main_info

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.RealEstateType
import com.openclassrooms.realestatemanager.databinding.FragmentEditMainInfoBinding
import com.openclassrooms.realestatemanager.ui.util.onAfterTextChanged
import com.openclassrooms.realestatemanager.ui.util.toCharSequence
import com.openclassrooms.realestatemanager.ui.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditMainInfoFragment : Fragment(R.layout.fragment_edit_main_info) {

    private val binding by viewBinding { FragmentEditMainInfoBinding.bind(it) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel = ViewModelProvider(this).get(EditMainInfoViewModel::class.java)

        val estateTypeArrayAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            RealEstateType.values().map { getString(it.labelId) }
        )
        binding.mainInfoTypeInput.setOnItemClickListener { parent, _, position, _ ->
            viewModel.onTypeSelected(parent.getItemAtPosition(position).toString())
        }

        binding.mainInfoPriceInput.onAfterTextChanged { text, cursorPosition ->
            viewModel.onPriceChanged(text?.toString(), cursorPosition)
        }
        binding.mainInfoAreaInput.onAfterTextChanged { text, cursorPosition ->
            viewModel.onAreaChanged(text?.toString(), cursorPosition)
        }

        binding.mainInfoAddTotalRoomButton.setOnClickListener { viewModel.onTotalRoomAdded() }
        binding.mainInfoRemoveTotalRoomButton.setOnClickListener { viewModel.onTotalRoomRemoved() }
        binding.mainInfoAddBathroomButton.setOnClickListener { viewModel.onBathRoomAdded() }
        binding.mainInfoRemoveBathroomButton.setOnClickListener { viewModel.onBathRoomRemoved() }
        binding.mainInfoAddBedroomButton.setOnClickListener { viewModel.onBedRoomAdded() }
        binding.mainInfoRemoveBedroomButton.setOnClickListener { viewModel.onBedRoomRemoved() }

        viewModel.viewStateLiveData.observe(viewLifecycleOwner) {
            binding.mainInfoTypeInput.setAdapter(estateTypeArrayAdapter)
            binding.mainInfoTypeInput.setText(
                it.selectedType.toCharSequence(requireContext()),
                false
            )

            binding.mainInfoPriceInput.setText(it.price)
            binding.mainInfoPriceInput.setSelection(it.priceSelection)

            binding.mainInfoAreaInput.setText(it.area)
            binding.mainInfoAreaInput.setSelection(it.areaSelection)

            binding.mainInfoTotalRoomCount.text = it.totalRoomCount
            binding.mainInfoBathroomCount.text = it.bathroomCount
            binding.mainInfoBedroomCount.text = it.bedroomCount

            binding.mainInfoTypeInputLayout.error = it.typeError
        }
    }
}
