package com.openclassrooms.realestatemanager.ui.list

import android.os.Bundle
import android.util.Log
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
        super.onViewCreated(view, savedInstanceState)

        val viewModel: ListViewModel by viewModels()

        val simpleAdapter = SimpleAdapter { viewModel.onItemClicked(it) }
        binding.listRv.adapter = simpleAdapter

        viewModel.viewState.observe(viewLifecycleOwner) {
            Log.d("Neige", "onViewCreated: observe view state: $it")
            simpleAdapter.submitList(it)
        }
    }
}