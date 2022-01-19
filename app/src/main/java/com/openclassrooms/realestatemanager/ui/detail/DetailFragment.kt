package com.openclassrooms.realestatemanager.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.openclassrooms.realestatemanager.databinding.FragmentDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment : Fragment() {

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
        ViewModelProvider(this)
            .get(DetailViewModel::class.java)
            .viewState
            .observe(viewLifecycleOwner) { binding.detailTv.text = it }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mutableBinding = null
    }
}