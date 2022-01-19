package com.openclassrooms.realestatemanager.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.openclassrooms.realestatemanager.databinding.FragmentListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListFragment : Fragment() {

    private var mutableBinding: FragmentListBinding? = null
    private val binding get() = mutableBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        mutableBinding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val viewModel: ListViewModel by viewModels()

        binding.listRv.adapter = SimpleAdapter { viewModel.onItemClicked(it) }
            .apply { submitList(listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mutableBinding = null
    }
}