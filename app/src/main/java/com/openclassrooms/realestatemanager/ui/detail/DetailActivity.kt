package com.openclassrooms.realestatemanager.ui.detail

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityDetailBinding
import com.openclassrooms.realestatemanager.ui.form.FormActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    private val viewModel: DetailParentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityDetailBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setSupportActionBar(binding.detailToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel.detailEventLiveData.observe(this) {
            when (it) {
                DetailEvent.Exit -> finish()
                DetailEvent.OpenForm -> startActivity(Intent(this, FormActivity::class.java))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onActivityResumed(resources.getBoolean(R.bool.is_tablet))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        R.id.main_menu_edit_estate -> {
            viewModel.onEditMenuItemClicked()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}