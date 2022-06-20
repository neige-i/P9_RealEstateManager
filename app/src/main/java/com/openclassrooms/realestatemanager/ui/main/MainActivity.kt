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

        /*
            This activity contains a MenuItem which visibility is modified.
            The modification occurs while observing the ViewModel's view state.
            To better access the MenuItem instance, the menu is not inflated by the activity as usual but by the toolbar.
            In this scenario, Activity.setSupportActionBar() should not be called, otherwise MenuItems won't be displayed.
         */

        filterBinding.filterToolbar.apply {
            setNavigationOnClickListener { onBackPressed() }
            inflateMenu(R.menu.menu_main)
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.main_menu_add_estate -> {
                        viewModel.onAddMenuItemClicked()
                        true
                    }
                    R.id.main_menu_edit_estate -> {
                        viewModel.onEditMenuItemClicked()
                        true
                    }
                    R.id.main_menu_filter_estate -> {
                        viewModel.onFilterMenuItemClicked()
                        true
                    }
                    else -> false
                }
            }
        }

        val editMenuItem = filterBinding.filterToolbar.menu.findItem(R.id.main_menu_edit_estate)

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

            filterBinding.filterList.isVisible = viewStateToolbar.isFilterLayoutVisible
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