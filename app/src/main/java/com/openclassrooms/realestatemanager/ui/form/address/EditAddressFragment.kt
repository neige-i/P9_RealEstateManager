package com.openclassrooms.realestatemanager.ui.form.address

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.flexbox.FlexboxLayoutManager
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.UtilsRepository
import com.openclassrooms.realestatemanager.databinding.FragmentEditAddressBinding
import com.openclassrooms.realestatemanager.ui.onAfterTextChanged
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

        binding.addressStreetInput.onAfterTextChanged { text, cursorPosition ->
            viewModel.onStreetNameChanged(text?.toString(), cursorPosition)
        }
        binding.addressAdditionalInput.onAfterTextChanged { text, cursorPosition ->
            viewModel.onAdditionalAddressInfoChanged(text?.toString(), cursorPosition)
        }
        binding.addressCityInput.onAfterTextChanged { text, cursorPosition ->
            viewModel.onCityChanged(text?.toString(), cursorPosition)
        }
        binding.addressStateInput.onAfterTextChanged { text, cursorPosition ->
            viewModel.onStateNameChanged(text?.toString(), cursorPosition)
        }
        binding.addressZipcodeInput.onAfterTextChanged { text, cursorPosition ->
            viewModel.onZipcodeChanged(text?.toString(), cursorPosition)
        }
        binding.addressCountryInput.onAfterTextChanged { text, cursorPosition ->
            viewModel.onCountryChanged(text?.toString(), cursorPosition)
        }

        viewModel.viewStateLiveData.observe(viewLifecycleOwner) {
            binding.addressStreetInput.setText(it.streetNumber)
            binding.addressStreetInput.setSelection(it.streetNumberSelection)
            binding.addressAdditionalInput.setText(it.additionalInfo)
            binding.addressAdditionalInput.setSelection(it.additionalInfoSelection)
            binding.addressCityInput.setText(it.city)
            binding.addressCityInput.setSelection(it.citySelection)
            binding.addressStateInput.setText(it.state, false)
            binding.addressStateInput.setSelection(it.stateSelection)
            binding.addressZipcodeInput.setText(it.zipcode)
            binding.addressZipcodeInput.setSelection(it.zipcodeSelection)
            binding.addressCountryInput.setText(it.country)
            binding.addressCountryInput.setSelection(it.countrySelection)

            binding.addressStreetInputLayout.error = it.streetNumberError
            binding.addressCityInputLayout.error = it.cityError
            binding.addressStateInputLayout.error = it.stateError
            binding.addressZipcodeInputLayout.error = it.zipcodeError
            binding.addressCountryInputLayout.error = it.countryError

            chipAdapter.submitList(it.pointOfInterestList)
        }
    }
}
