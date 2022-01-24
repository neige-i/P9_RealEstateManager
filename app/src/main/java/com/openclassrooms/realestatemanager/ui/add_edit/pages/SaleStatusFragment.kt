package com.openclassrooms.realestatemanager.ui.add_edit.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.openclassrooms.realestatemanager.databinding.FragmentSaleStatusBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SaleStatusFragment : Fragment() {

    private var mutableBinding: FragmentSaleStatusBinding? = null
    private val binding get() = mutableBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        mutableBinding = FragmentSaleStatusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel = ViewModelProvider(this).get(SaleStatusViewModel::class.java)

        binding.statusSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onAvailableSwitched(isChecked)
        }

        viewModel.viewState.observe(viewLifecycleOwner) { isAvailable ->
            binding.statusSwitch.isChecked = isAvailable
            binding.soldDateInputLayout.isVisible = !isAvailable
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mutableBinding = null
    }
}
