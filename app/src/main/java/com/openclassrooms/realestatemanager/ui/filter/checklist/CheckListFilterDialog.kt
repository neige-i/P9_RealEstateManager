package com.openclassrooms.realestatemanager.ui.filter.checklist

import androidx.fragment.app.viewModels
import com.openclassrooms.realestatemanager.databinding.FragmentListBinding
import com.openclassrooms.realestatemanager.ui.filter.FilterDialog
import com.openclassrooms.realestatemanager.ui.util.viewBinding

class CheckListFilterDialog private constructor() : FilterDialog() {

    override val binding by viewBinding(FragmentListBinding::inflate)
    override val viewModel by viewModels<CheckListFilterViewModel>()

    private val checkListAdapter = CheckListAdapter()

    override fun initUi() {
        binding.root.adapter = checkListAdapter
    }

    override fun updateViewState() {
        viewModel.viewState.observe(this) { checkList ->
            dialog?.setTitle(checkList.dialogTitle)
            checkListAdapter.submitList(checkList.items)
        }
    }

    override fun triggerViewEvents() {}
}