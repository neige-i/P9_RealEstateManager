package com.openclassrooms.realestatemanager.ui.detail

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.flexbox.FlexboxLayoutManager
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentDetailBinding
import com.openclassrooms.realestatemanager.ui.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment : Fragment(R.layout.fragment_detail) {

    private val binding by viewBinding { FragmentDetailBinding.bind(it) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel: DetailViewModel by viewModels()

        val mediaAdapter = MediaAdapter {
            viewModel.onMediaClicked(it)
        }
        binding.detailMediaList.adapter = mediaAdapter

        val chipAdapter = ChipAdapter()
        binding.detailPoiList.adapter = chipAdapter
        binding.detailPoiList.layoutManager = FlexboxLayoutManager(requireContext())

        viewModel.viewState.observe(viewLifecycleOwner) {
            binding.detailNoSelectionLbl.isVisible = it.isNoSelectionLabelVisible

            when (it) {
                is DetailViewState.Empty -> {
                    binding.detailNoSelectionLbl.text = it.noSelectionLabelText
                }
                is DetailViewState.Info -> {
                    binding.detailAvailabilityLbl.text = it.saleText
                    binding.detailAvailabilityLbl.backgroundTintList =
                        ContextCompat.getColorStateList(
                            requireContext(),
                            it.saleBackgroundColor
                        )

                    binding.detailDescriptionTxt.text = it.description

                    mediaAdapter.submitList(it.photoList)

                    binding.detailSurfaceTxt.text = it.surface
                    binding.detailRoomTxt.text = it.roomCount
                    binding.detailBathTxt.text = it.bathroomCount
                    binding.detailBedTxt.text = it.bedroomCount

                    binding.detailLocationTxt.text = it.address
                    chipAdapter.submitList(it.poiList)

                    binding.detailMarketTxt.text = it.market_dates

                    binding.detailAgentTxt.text = it.agentName
                }
            }
        }
    }
}