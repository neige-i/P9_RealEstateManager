package com.openclassrooms.realestatemanager.ui.filter

import android.app.Dialog
import android.widget.ArrayAdapter
import android.widget.CheckedTextView
import android.widget.ListView
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.openclassrooms.realestatemanager.R

class MultiChoiceFilterDialog : FilterDialog() {

    override fun getFilterDialog(): Dialog {
        val viewModel = ViewModelProvider(this).get(MultiChoiceFilterViewModel::class.java)

        // Impossible to setMultiChoiceItems after the dialog is displayed
        // Instead pass to the dialog an adapter that will be modified afterwards
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_multiple_choice,
            arrayListOf<String>(), // Initialize with a MUTABLE empty list
        )

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("dummy non-empty text")
            .setPositiveButton(R.string.apply_filter) { dialog, which -> viewModel.onPositiveButtonClicked() }
            .setNegativeButton(R.string.cancel_filter, null)
            .setNeutralButton(R.string.reset_filter, null) // Behavior overridden in OnShowListener
            .setAdapter(adapter, null) // Behaviour overridden in AdapterView.OnItemClickListener
            .create()
            .apply {
                // Manually set choiceMode instead of calling setMultiChoiceItems
                listView.choiceMode = ListView.CHOICE_MODE_MULTIPLE
                listView.setOnItemClickListener { _, view, position, _ ->
                    viewModel.onItemClicked(position, (view as CheckedTextView).isChecked)
                }
                setOnShowListener {
                    // getButton() return null if dialog is not shown yet
                    getButton(Dialog.BUTTON_NEUTRAL).setOnClickListener {
                        viewModel.onNeutralButtonClicked()
                    }
                }
            }

        viewModel.viewState.observe(this) { multiChoice ->
            dialog.setTitle(multiChoice.dialogTitle)

            adapter.clear()
            adapter.addAll(multiChoice.labels)

            multiChoice.checkedItems.forEachIndexed { index, isChecked ->
                dialog.listView.setItemChecked(index, isChecked)
            }
        }

        return dialog
    }
}