package com.openclassrooms.realestatemanager.list

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.openclassrooms.realestatemanager.ShowDetailCallback
import com.openclassrooms.realestatemanager.databinding.FragmentListBinding
import com.openclassrooms.realestatemanager.main.MainActivity

class ListFragment : Fragment() {

    private var mutableBinding: FragmentListBinding? = null
    private val binding get() = mutableBinding!!

    private lateinit var showDetailCallback: ShowDetailCallback

    override fun onAttach(context: Context) {
        super.onAttach(context)
        showDetailCallback = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        mutableBinding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.listRv.adapter = SimpleAdapter { showDetailCallback.onShowDetail(it) }
            .also {
                it.submitList(listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"))
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mutableBinding = null
    }
}