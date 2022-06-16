package com.openclassrooms.realestatemanager.ui.filter

import androidx.fragment.app.viewModels
import com.openclassrooms.realestatemanager.data.filter.FilterType
import com.openclassrooms.realestatemanager.data.filter.FilterValue
import com.openclassrooms.realestatemanager.databinding.FragmentListBinding
import com.openclassrooms.realestatemanager.ui.util.viewBinding

class MultiChoiceFilterDialog private constructor() : FilterDialog() {

    companion object {
        fun newInstance(filterType: FilterType, filterValue: FilterValue?) = MultiChoiceFilterDialog().createInstance(filterType, filterValue)
    }

    override val binding by viewBinding(FragmentListBinding::inflate)
    override val viewModel by viewModels<MultiChoiceFilterViewModel>()

    private val checkListAdapter = CheckListAdapter()

    override fun initUi() {
        binding.root.adapter = checkListAdapter
    }

    override fun updateViewState() {
        viewModel.viewState.observe(this) { multiChoice ->
            dialog?.setTitle(multiChoice.dialogTitle)
            checkListAdapter.submitList(multiChoice.checkItems)
        }
    }

    override fun triggerViewEvents() {
    }
}