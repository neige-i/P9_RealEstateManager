package com.openclassrooms.realestatemanager.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityMainBinding
import com.openclassrooms.realestatemanager.ui.add_edit.AddEditActivity
import com.openclassrooms.realestatemanager.ui.detail.DetailActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.mainToolbar)

        viewModel.mainEventLiveData.observe(this) {
            when (it) {
                MainEvent.GoToDetailActivity -> redirectTo(DetailActivity::class.java)
                MainEvent.GoToAddActivity -> redirectTo(AddEditActivity::class.java)
            }
        }
    }

    private fun redirectTo(activityToStart: Class<out AppCompatActivity>) {
        startActivity(Intent(this, activityToStart))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return viewModel.onMenuItemClicked(item.itemId)
    }
}