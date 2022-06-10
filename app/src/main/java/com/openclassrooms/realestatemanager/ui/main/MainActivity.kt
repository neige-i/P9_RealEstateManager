package com.openclassrooms.realestatemanager.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityMainBinding
import com.openclassrooms.realestatemanager.ui.detail.DetailFragment
import com.openclassrooms.realestatemanager.ui.filter.checklist.CheckListFilterDialog
import com.openclassrooms.realestatemanager.ui.filter.date.DateFilterDialog
import com.openclassrooms.realestatemanager.ui.filter.slider.SliderFilterDialog
import com.openclassrooms.realestatemanager.ui.form.FormActivity
import com.openclassrooms.realestatemanager.ui.main.MainEvent.*
import com.openclassrooms.realestatemanager.ui.util.setNavigationIcon
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
            val viewStateToolbar = mainViewState.toolbar

            filterBinding.filterToolbar.apply {
                setTitle(viewStateToolbar.title)
                setNavigationIcon(viewStateToolbar.navIcon)
            }

            editMenuItem?.isVisible = mainViewState.isEditMenuItemVisible

            filterBinding.filterList.isVisible = viewStateToolbar.isFiltering
            filterAdapter.submitList(mainViewState.chips)
        }

        viewModel.mainEventLiveData.observe(this) { mainEvent ->
            when (mainEvent) {
                is OpenEstateDetail -> supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace<DetailFragment>(R.id.unique_content)
                    addToBackStack(null)
                }
                is OpenEstateForm -> startActivity(Intent(this, FormActivity::class.java))
                is ShowSliderFilterSettings -> {
                    SliderFilterDialog.newInstance(mainEvent.filterType, mainEvent.filterValue).show(supportFragmentManager, null)
                }
                is ShowCheckListFilterSettings -> {
                    CheckListFilterDialog.newInstance(mainEvent.filterType, mainEvent.filterValue).show(supportFragmentManager, null)
                }
                is ShowDateFilterSettings -> {
                    DateFilterDialog.newInstance(mainEvent.filterType, mainEvent.filterValue).show(supportFragmentManager, null)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onActivityResumed()
    }
}