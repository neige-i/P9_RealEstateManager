package com.openclassrooms.realestatemanager.ui.list

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentListBinding
import com.openclassrooms.realestatemanager.ui.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListFragment : Fragment(R.layout.fragment_list) {

    private val binding by viewBinding { FragmentListBinding.bind(it) }
    private val viewModel by viewModels<ListViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val estateAdapter = EstateAdapter()
        binding.estateList.apply {
            adapter = estateAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        viewModel.viewStateLiveData.observe(viewLifecycleOwner) { estateList ->
            estateAdapter.submitList(estateList)
        }
    }
}