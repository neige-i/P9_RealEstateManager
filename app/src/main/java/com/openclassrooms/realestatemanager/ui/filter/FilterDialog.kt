package com.openclassrooms.realestatemanager.ui.filter

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
abstract class FilterDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return getFilterDialog()
    }

    abstract fun getFilterDialog(): Dialog
}