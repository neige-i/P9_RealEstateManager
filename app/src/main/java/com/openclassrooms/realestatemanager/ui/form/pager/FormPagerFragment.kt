package com.openclassrooms.realestatemanager.ui.form.pager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.openclassrooms.realestatemanager.databinding.FragmentFormPagerBinding
import com.openclassrooms.realestatemanager.ui.form.FormPagerAdapter
import com.openclassrooms.realestatemanager.ui.form.address.EditAddressFragment
import com.openclassrooms.realestatemanager.ui.form.detail_info.EditDetailInfoFragment
import com.openclassrooms.realestatemanager.ui.form.main_info.EditMainInfoFragment
import com.openclassrooms.realestatemanager.ui.form.sale.EditSaleFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FormPagerFragment : Fragment() {

    private var mutableBinding: FragmentFormPagerBinding? = null
    private val binding: FragmentFormPagerBinding get() = mutableBinding!!

    private val viewModel: FormPagerViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        mutableBinding = FragmentFormPagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val addEditPagerAdapter = FormPagerAdapter(
            fragmentActivity = requireActivity(),
            fragmentList = listOf(
                EditMainInfoFragment(),
                EditDetailInfoFragment(),
                EditAddressFragment(),
                EditSaleFragment()
            ),
        )
        viewModel.onInitPagerAdapter(addEditPagerAdapter.itemCount)

        binding.formPager.isUserInputEnabled = false
        binding.formPager.adapter = addEditPagerAdapter

        binding.formSubmitButton.setOnClickListener { viewModel.onSubmitButtonClicked() }

        viewModel.viewStateLiveData.observe(viewLifecycleOwner) {
            binding.formSubmitButton.text = it.toolbarTitle
            binding.formPager.currentItem = it.pagerPosition
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mutableBinding = null
    }
}