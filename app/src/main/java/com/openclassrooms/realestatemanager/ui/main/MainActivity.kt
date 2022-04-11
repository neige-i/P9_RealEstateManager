package com.openclassrooms.realestatemanager.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityMainBinding
import com.openclassrooms.realestatemanager.ui.detail.DetailActivity
import com.openclassrooms.realestatemanager.ui.form.FormActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private var editMenuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.mainToolbar)

        viewModel.viewStateLiveData.observe(this) {
            editMenuItem?.isVisible = it.isEditMenuItemVisible
        }
        viewModel.mainEventLiveData.observe(this) {
            when (it) {
                MainEvent.GoToDetailActivity -> redirectTo(DetailActivity::class.java)
                MainEvent.GoToFormActivity -> redirectTo(FormActivity::class.java)
            }
        }
    }

    private fun redirectTo(activityToStart: Class<out AppCompatActivity>) {
        startActivity(Intent(this, activityToStart))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        editMenuItem = menu?.findItem(R.id.main_menu_edit_estate)
        viewModel.onOptionsMenuCreated()

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.main_menu_edit_estate -> {
            viewModel.onEditMenuItemClicked()
            true
        }
        R.id.main_menu_add_estate -> {
            viewModel.onAddMenuItemClicked()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}