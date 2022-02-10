package com.openclassrooms.realestatemanager.ui.form.address

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.flexbox.FlexboxLayoutManager
import com.openclassrooms.realestatemanager.databinding.FragmentEditAddressBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditAddressFragment : Fragment() {

    private var mutableBinding: FragmentEditAddressBinding? = null
    private val binding get() = mutableBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        mutableBinding = FragmentEditAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel = ViewModelProvider(this).get(EditAddressViewModel::class.java)

        val chipAdapter = ChipAdapter { name, isChecked -> viewModel.onPoiChecked(name, isChecked) }

        binding.poiList.adapter = chipAdapter
        binding.poiList.layoutManager = FlexboxLayoutManager(requireContext())

        binding.addressStreetInput.doAfterTextChanged {
            viewModel.onStreetNameChanged(it?.toString())
        }
        binding.addressAdditionalInput.doAfterTextChanged {
            viewModel.onAdditionalAddressInfoChanged(it?.toString())
        }
        binding.addressCityInput.doAfterTextChanged {
            viewModel.onCityChanged(it?.toString())
        }
        binding.addressStateInput.doAfterTextChanged {
            viewModel.onStateNameChanged(it?.toString())
        }
        binding.addressZipcodeInput.doAfterTextChanged {
            viewModel.onZipcodeChanged(it?.toString())
        }
        binding.addressCountryInput.doAfterTextChanged {
            viewModel.onCountryChanged(it?.toString())
        }

        viewModel.viewStateLiveData.observe(viewLifecycleOwner) {
            binding.addressStreetInput.setText(it.streetNumber)
            binding.addressAdditionalInput.setText(it.additionalInfo)
            binding.addressCityInput.setText(it.city)
            binding.addressStateInput.setText(it.state)
            binding.addressZipcodeInput.setText(it.zipcode)
            binding.addressCountryInput.setText(it.country)

            chipAdapter.submitList(it.pointOfInterestList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mutableBinding = null
    }
}
