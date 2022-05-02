package com.openclassrooms.realestatemanager.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityMainBinding
import com.openclassrooms.realestatemanager.ui.detail.DetailFragment
import com.openclassrooms.realestatemanager.ui.form.FormActivity
import com.openclassrooms.realestatemanager.ui.main.MainEvent.OpenEstateDetail
import com.openclassrooms.realestatemanager.ui.main.MainEvent.OpenEstateForm
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // Do not set support Actionbar when inflate menu from the Toolbar
        // Otherwise MenuItems won't be displayed

        binding.mainToolbar.setNavigationOnClickListener { onBackPressed() }
        binding.mainToolbar.inflateMenu(R.menu.menu_main)
        binding.mainToolbar
            .menu
            .findItem(R.id.main_menu_add_estate)
            .setOnMenuItemClickListener {
                viewModel.onAddMenuItemClicked()
                true
            }
        val editMenuItem = binding.mainToolbar
            .menu
            .findItem(R.id.main_menu_edit_estate)
            .setOnMenuItemClickListener {
                viewModel.onEditMenuItemClicked()
                true
            }

        supportFragmentManager.addOnBackStackChangedListener {
            viewModel.onBackStackChanged(supportFragmentManager.backStackEntryCount)
        }

        viewModel.viewStateLiveData.observe(this) { mainViewState ->
            binding.mainToolbar.title = mainViewState.toolbarTitle
            binding.mainToolbar.navigationIcon = if (mainViewState.navigationIconId != null) {
                ContextCompat.getDrawable(this, mainViewState.navigationIconId)
            } else {
                null
            }
            editMenuItem?.isVisible = mainViewState.isEditMenuItemVisible
        }

        viewModel.mainEventLiveData.observe(this) { mainEvent ->
            when (mainEvent) {
                OpenEstateDetail -> supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace<DetailFragment>(R.id.main_content)
                    addToBackStack(null)
                }
                OpenEstateForm -> startActivity(Intent(this, FormActivity::class.java))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onActivityResumed()
    }
}