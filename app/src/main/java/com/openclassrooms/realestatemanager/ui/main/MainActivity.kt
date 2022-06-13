package com.openclassrooms.realestatemanager.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityMainBinding
import com.openclassrooms.realestatemanager.ui.detail.DetailFragment
import com.openclassrooms.realestatemanager.ui.filter.DateFilterDialog
import com.openclassrooms.realestatemanager.ui.filter.MultiChoiceFilterDialog
import com.openclassrooms.realestatemanager.ui.filter.RangeFilterDialog
import com.openclassrooms.realestatemanager.ui.form.FormActivity
import com.openclassrooms.realestatemanager.ui.main.MainEvent.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        val filterBinding = binding.filterLayout

        setContentView(binding.root)

        // Do not set support Actionbar when inflate menu from the Toolbar
        // Otherwise MenuItems won't be displayed

        filterBinding.filterToolbar.setNavigationOnClickListener { onBackPressed() }
        filterBinding.filterToolbar.inflateMenu(R.menu.menu_main)
        filterBinding.filterToolbar.menu
            .findItem(R.id.main_menu_add_estate)
            .setOnMenuItemClickListener {
                viewModel.onAddMenuItemClicked()
                true
            }
        val editMenuItem = filterBinding.filterToolbar.menu
            .findItem(R.id.main_menu_edit_estate)
            .setOnMenuItemClickListener {
                viewModel.onEditMenuItemClicked()
                true
            }
        filterBinding.filterToolbar.menu
            .findItem(R.id.main_menu_filter_estate)
            .setOnMenuItemClickListener {
                viewModel.onFilterMenuItemClicked()
                true
            }

        val filterAdapter = FilterAdapter()
        filterBinding.filterList.adapter = filterAdapter

        supportFragmentManager.addOnBackStackChangedListener {
            viewModel.onBackStackChanged(supportFragmentManager.backStackEntryCount)
        }

        viewModel.viewStateLiveData.observe(this) { mainViewState ->
            filterBinding.filterToolbar.title = mainViewState.toolbarTitle
            filterBinding.filterToolbar.navigationIcon =
                if (mainViewState.navigationIconId != null) {
                    ContextCompat.getDrawable(this, mainViewState.navigationIconId)
                } else {
                    null
                }
            editMenuItem?.isVisible = mainViewState.isEditMenuItemVisible

            filterBinding.filterList.isVisible = mainViewState.isFiltering
            filterAdapter.submitList(mainViewState.chips)
        }

        viewModel.mainEventLiveData.observe(this) { mainEvent ->
            when (mainEvent) {
                is OpenEstateDetail -> supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace<DetailFragment>(R.id.main_content)
                    addToBackStack(null)
                }
                is OpenEstateForm -> startActivity(Intent(this, FormActivity::class.java))
                is ShowSliderFilterDialog -> {
                    RangeFilterDialog.newInstance(mainEvent.filterType, mainEvent.minMaxFilterValue).show(supportFragmentManager, null)
                }
                is ShowCheckableFilterDialog -> {
                    MultiChoiceFilterDialog.newInstance(mainEvent.filterType, mainEvent.choicesFilterValue).show(supportFragmentManager, null)
                }
                is ShowCalendarFilterDialog -> {
                    DateFilterDialog.newInstance(mainEvent.availableDatesFilterValue).show(supportFragmentManager, null)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onActivityResumed()
    }
}