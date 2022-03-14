package com.openclassrooms.realestatemanager.ui.form.picture

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityPictureBinding
import com.openclassrooms.realestatemanager.ui.form.image_launcher.ImageLauncherActivity
import com.openclassrooms.realestatemanager.ui.form.picker_dialog.PicturePickerDialog
import com.openclassrooms.realestatemanager.ui.util.onAfterTextChanged
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PictureActivity : ImageLauncherActivity() {

    private val viewModel: PictureViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityPictureBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.pictureToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.pictureDescriptionInput.onAfterTextChanged { text, cursorPosition ->
            viewModel.onDescriptionChanged(text?.toString(), cursorPosition)
        }

        viewModel.viewStateLiveData.observe(this) {
            Glide.with(this)
                .load(it.uri)
                .into(binding.pictureImage)

            binding.pictureDescriptionInput.setText(it.description)
            binding.pictureDescriptionInput.setSelection(it.descriptionSelection)
            binding.pictureDescriptionInputLayout.error = it.descriptionError
        }

        viewModel.exitEventLiveData.observe(this) { finish() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_picture, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        R.id.save_picture -> {
            viewModel.onSaveMenuItemClicked()
            true
        }
        R.id.edit_picture -> {
            PicturePickerDialog().show(supportFragmentManager, null)
            true
        }
        else -> false
    }

    // Override finish() instead of onDestroy() to reset the repository's picture
    // The parent activity observes the repository's picture and starts this activity if not null
    // So the picture must be null BEFORE returning back to the parent activity
    // But onDestroy() is called AFTER the parent activity is resumed
    override fun finish() {
        super.finish()
        viewModel.onActivityFinished()
    }
}