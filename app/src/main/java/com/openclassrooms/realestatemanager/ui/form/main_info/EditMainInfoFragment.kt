package com.openclassrooms.realestatemanager.ui.form.main_info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.openclassrooms.realestatemanager.data.RealEstateType
import com.openclassrooms.realestatemanager.databinding.FragmentEditMainInfoBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditMainInfoFragment : Fragment() {

    private var mutableBinding: FragmentEditMainInfoBinding? = null
    private val binding get() = mutableBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        mutableBinding = FragmentEditMainInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

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

        binding.mainInfoPriceInput.doAfterTextChanged { viewModel.onPriceChanged(it?.toString()) }
        binding.mainInfoAreaInput.doAfterTextChanged { viewModel.onAreaChanged(it?.toString()) }

        binding.mainInfoAddTotalRoomButton.setOnClickListener { viewModel.onTotalRoomAdded() }
        binding.mainInfoRemoveTotalRoomButton.setOnClickListener { viewModel.onTotalRoomRemoved() }
        binding.mainInfoAddBathroomButton.setOnClickListener { viewModel.onBathRoomAdded() }
        binding.mainInfoRemoveBathroomButton.setOnClickListener { viewModel.onBathRoomRemoved() }
        binding.mainInfoAddBedroomButton.setOnClickListener { viewModel.onBedRoomAdded() }
        binding.mainInfoRemoveBedroomButton.setOnClickListener { viewModel.onBedRoomRemoved() }

        viewModel.viewStateLiveData.observe(viewLifecycleOwner) {
            binding.mainInfoTypeInput.setAdapter(estateTypeArrayAdapter)
            binding.mainInfoTypeInput.setText(it.selectedType, false)
            binding.mainInfoPriceInput.setText(it.price)
            binding.mainInfoAreaInput.setText(it.area)
            binding.mainInfoTotalRoomCount.text = it.totalRoomCount
            binding.mainInfoBathroomCount.text = it.bathroomCount
            binding.mainInfoBedroomCount.text = it.bedroomCount

            binding.mainInfoTypeInputLayout.error = it.typeError
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mutableBinding = null
    }
}
