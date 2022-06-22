package com.openclassrooms.realestatemanager.ui.form.address

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.flexbox.FlexboxLayoutManager
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.UtilsRepository
import com.openclassrooms.realestatemanager.databinding.FragmentEditAddressBinding
import com.openclassrooms.realestatemanager.ui.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditAddressFragment : Fragment(R.layout.fragment_edit_address) {

    private val binding by viewBinding { FragmentEditAddressBinding.bind(it) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel = ViewModelProvider(this).get(EditAddressViewModel::class.java)

        val chipAdapter = ChipAdapter { name, isChecked -> viewModel.onPoiChecked(name, isChecked) }

        binding.poiList.adapter = chipAdapter
        binding.poiList.layoutManager = FlexboxLayoutManager(requireContext())

        binding.addressStateInput.setAdapter(
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                UtilsRepository.STATE_POSTAL_ABBR
            )
        )

        binding.addressStreetInput.doAfterTextChanged { viewModel.onStreetNameChanged(it?.toString()) }
        binding.addressAdditionalInput.doAfterTextChanged { viewModel.onAdditionalAddressInfoChanged(it?.toString()) }
        binding.addressCityInput.doAfterTextChanged { viewModel.onCityChanged(it?.toString()) }
        binding.addressStateInput.doAfterTextChanged { viewModel.onStateNameChanged(it?.toString()) }
        binding.addressZipcodeInput.doAfterTextChanged { viewModel.onZipcodeChanged(it?.toString()) }
        binding.addressCountryInput.doAfterTextChanged { viewModel.onCountryChanged(it?.toString()) }

        viewModel.viewStateLiveData.observe(viewLifecycleOwner) {
            binding.addressStreetInput.setTextKeepState(it.streetNumber)
            binding.addressAdditionalInput.setTextKeepState(it.additionalInfo)
            binding.addressCityInput.setTextKeepState(it.city)
            binding.addressStateInput.setTextKeepState(it.state)
            binding.addressZipcodeInput.setTextKeepState(it.zipcode)
            binding.addressCountryInput.setTextKeepState(it.country)

            binding.addressStreetInputLayout.error = it.streetNumberError
            binding.addressCityInputLayout.error = it.cityError
            binding.addressStateInputLayout.error = it.stateError
            binding.addressZipcodeInputLayout.error = it.zipcodeError
            binding.addressCountryInputLayout.error = it.countryError

            chipAdapter.submitList(it.pointOfInterestList)
        }
    }
}
