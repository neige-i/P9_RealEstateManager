package com.openclassrooms.realestatemanager.ui.detail

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.android.flexbox.FlexboxLayoutManager
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentDetailBinding
import com.openclassrooms.realestatemanager.ui.detail.DetailViewState.Empty
import com.openclassrooms.realestatemanager.ui.detail.DetailViewState.WithInfo
import com.openclassrooms.realestatemanager.ui.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment : Fragment(R.layout.fragment_detail) {

    private val binding by viewBinding { FragmentDetailBinding.bind(it) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel: DetailViewModel by viewModels()

        val photoAdapter = PhotoAdapter { selectedPhoto -> viewModel.onPhotoClicked(selectedPhoto) }
        binding.detailPhotoList.adapter = photoAdapter

        val chipAdapter = ChipAdapter()
        binding.detailPoiList.adapter = chipAdapter
        binding.detailPoiList.layoutManager = FlexboxLayoutManager(requireContext())

        viewModel.viewStateLiveData.observe(viewLifecycleOwner) { detailViewState ->
            binding.detailNoSelectionLbl.isVisible = detailViewState.isNoSelectionLabelVisible

            when (detailViewState) {
                is Empty -> {
                    binding.detailNoSelectionLbl.text = detailViewState.noSelectionLabelText
                }
                is WithInfo -> {
                    binding.detailTypeTxt.text = detailViewState.type
                    binding.detailTypeTxt.isVisible = detailViewState.areTypeAndPriceVisible
                    binding.detailPriceTxt.text = detailViewState.price
                    binding.detailPriceTxt.isVisible = detailViewState.areTypeAndPriceVisible

                    binding.detailAvailabilityTxt.text = detailViewState.saleText
                    binding.detailAvailabilityTxt.backgroundTintList =
                        ContextCompat.getColorStateList(
                            requireContext(),
                            detailViewState.saleBackgroundColor
                        )

                    binding.detailDescriptionTxt.text = detailViewState.description

                    photoAdapter.submitList(detailViewState.photoList)

                    binding.detailSurfaceTxt.text = detailViewState.surface
                    binding.detailRoomTxt.text = detailViewState.roomCount
                    binding.detailBathTxt.text = detailViewState.bathroomCount
                    binding.detailBedTxt.text = detailViewState.bedroomCount

                    binding.detailLocationTxt.text = detailViewState.address
                    chipAdapter.submitList(detailViewState.poiList)
                    Glide.with(binding.root)
                        .load(detailViewState.mapUrl)
                        .into(binding.detailMapImg)

                    binding.detailMarketTxt.text = detailViewState.market_dates

                    binding.detailAgentTxt.text = detailViewState.agentName
                }
            }
        }
    }
}