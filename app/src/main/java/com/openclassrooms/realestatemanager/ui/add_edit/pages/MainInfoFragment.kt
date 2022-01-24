package com.openclassrooms.realestatemanager.ui.add_edit.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentMainInfoBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainInfoFragment : Fragment() {

    private var mutableBinding: FragmentMainInfoBinding? = null
    private val binding get() = mutableBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        mutableBinding = FragmentMainInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel = ViewModelProvider(this).get(MainInfoViewModel::class.java)

        val estateTypeArrayAdapter =ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            listOf(
                getString(R.string.real_estate_type_flat),
                getString(R.string.real_estate_type_duplex),
                getString(R.string.real_estate_type_house),
                getString(R.string.real_estate_type_penthouse),
            )
        )

        binding.mainInfoAddTotalRoomButton.setOnClickListener { viewModel.onTotalRoomAdded() }
        binding.mainInfoAddBathroomButton.setOnClickListener { viewModel.onBathRoomAdded() }
        binding.mainInfoAddBedroomButton.setOnClickListener { viewModel.onBedRoomAdded() }
        binding.mainInfoRemoveTotalRoomButton.setOnClickListener { viewModel.onTotalRoomRemoved() }
        binding.mainInfoRemoveBathroomButton.setOnClickListener { viewModel.onBathRoomRemoved() }
        binding.mainInfoRemoveBedroomButton.setOnClickListener { viewModel.onBedRoomRemoved() }

        viewModel.viewStateLiveData.observe(viewLifecycleOwner) {
            binding.mainInfoTypeInput.setAdapter(estateTypeArrayAdapter)

            binding.mainInfoTotalRoomCount.text = it.totalRoomCount.toString()
            binding.mainInfoBathroomCount.text = it.bathroomCount.toString()
            binding.mainInfoBedroomCount.text = it.bedroomCount.toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mutableBinding = null
    }
}
