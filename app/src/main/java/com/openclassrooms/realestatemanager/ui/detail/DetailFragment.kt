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
import com.openclassrooms.realestatemanager.ui.detail.DetailViewState.Info
import com.openclassrooms.realestatemanager.ui.util.toCharSequence
import com.openclassrooms.realestatemanager.ui.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment : Fragment(R.layout.fragment_detail) {

    private val binding by viewBinding { FragmentDetailBinding.bind(it) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel: DetailViewModel by viewModels()

        val photoAdapter = PhotoAdapter()
        binding.detailPhotoList.adapter = photoAdapter

        val chipAdapter = ChipAdapter()
        binding.detailPoiList.adapter = chipAdapter
        binding.detailPoiList.layoutManager = FlexboxLayoutManager(requireContext())

        viewModel.viewStateLiveData.observe(viewLifecycleOwner) { detail ->
            binding.detailNoSelectionLbl.isVisible = detail.isNoSelectionLabelVisible

            when (detail) {
                is Empty -> {
                    binding.detailNoSelectionLbl.text =
                        detail.noSelectionLabelText.toCharSequence(requireContext())
                }
                is Info -> {
                    binding.detailTypeTxt.setText(detail.type)
                    binding.detailTypeTxt.isVisible = detail.areTypeAndPriceVisible
                    binding.detailPriceTxt.text = detail.price.toCharSequence(requireContext())
                    binding.detailPriceTxt.isVisible = detail.areTypeAndPriceVisible

                    binding.detailAvailabilityTxt.setText(detail.saleText)
                    binding.detailAvailabilityTxt.backgroundTintList =
                        ContextCompat.getColorStateList(
                            requireContext(),
                            detail.saleBackgroundColor
                        )

                    binding.detailDescriptionTxt.text = detail.description.toCharSequence(requireContext())

                    photoAdapter.submitList(detail.photoList)

                    binding.detailSurfaceTxt.text = detail.surface.toCharSequence(requireContext())
                    binding.detailRoomTxt.text = detail.roomCount
                    binding.detailBathTxt.text = detail.bathroomCount
                    binding.detailBedTxt.text = detail.bedroomCount

                    binding.detailLocationTxt.text = detail.address
                    chipAdapter.submitList(detail.poiList)
                    Glide.with(binding.root)
                        .load(detail.mapUrl)
                        .into(binding.detailMapImg)

                    binding.detailMarketTxt.text = detail.marketDates.toCharSequence(requireContext())

                    binding.detailAgentTxt.text = detail.agentName.toCharSequence(requireContext())
                }
            }
        }
    }
}