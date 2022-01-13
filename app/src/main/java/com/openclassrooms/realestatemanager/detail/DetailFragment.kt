package com.openclassrooms.realestatemanager.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.openclassrooms.realestatemanager.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {

    companion object {
        const val ITEM_ARG = "ITEM_ARG"
    }

    private var mutableBinding: FragmentDetailBinding? = null
    private val binding get() = mutableBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        mutableBinding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.detailTv.text = "No item selected"

        arguments?.let {
            binding.detailTv.text = it.getString(ITEM_ARG)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mutableBinding = null
    }
}