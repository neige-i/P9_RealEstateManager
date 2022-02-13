package com.openclassrooms.realestatemanager.ui.form.picture

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityPictureBinding
import com.openclassrooms.realestatemanager.ui.form.picker_dialog.ImagePickerLifecycleObserver
import com.openclassrooms.realestatemanager.ui.form.picker_dialog.ImagePickerDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PictureActivity : AppCompatActivity() {

    private val viewModel: PictureViewModel by viewModels()
    private lateinit var imagePickerLifecycleObserver: ImagePickerLifecycleObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        imagePickerLifecycleObserver = ImagePickerLifecycleObserver(
            registry = activityResultRegistry,
            context = this
        ) { viewModel.onPhotoPicked(it) }
        lifecycle.addObserver(imagePickerLifecycleObserver)

        val binding = ActivityPictureBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.pictureToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.pictureDescriptionInput.doAfterTextChanged {
            viewModel.onDescriptionChanged(it?.toString())
        }

        viewModel.viewStateLiveData.observe(this) {
            Glide.with(this)
                .load(it.uri)
                .into(binding.pictureImage)

            binding.pictureDescriptionInput.setText(it.description)
            binding.pictureDescriptionInputLayout.error = it.descriptionError
        }

        viewModel.exitEvent.observe(this) { finish() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_picture, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.save_picture -> {
                viewModel.onSaveMenuItemClicked()
                true
            }
            R.id.edit_picture -> {
                ImagePickerDialog(imagePickerLifecycleObserver).show(supportFragmentManager, null)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun finish() {
        super.finish()
        viewModel.onActivityFinished()
    }
}