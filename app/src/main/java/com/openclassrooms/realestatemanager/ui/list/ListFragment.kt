package com.openclassrooms.realestatemanager.ui.list

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentListBinding
import com.openclassrooms.realestatemanager.ui.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListFragment : Fragment(R.layout.fragment_list) {

    private val binding by viewBinding { FragmentListBinding.bind(it) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val viewModel: ListViewModel by viewModels()

        binding.listRv.adapter = SimpleAdapter { viewModel.onItemClicked(it) }
            .apply { submitList(listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")) }
    }
}