package com.openclassrooms.realestatemanager.ui.form.picture

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.openclassrooms.realestatemanager.BuildConfig
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityPictureBinding
import com.openclassrooms.realestatemanager.ui.util.toCharSequence
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class PhotoActivity : AppCompatActivity() {

    private val viewModel: PhotoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityPictureBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.pictureToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.pictureDescriptionInput.doAfterTextChanged { viewModel.onDescriptionChanged(it?.toString()) }

        var cameraPictureUri: Uri? = null

        val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) {
            viewModel.onPhotoPicked(uri = cameraPictureUri, success = it)
        }
        val galleryLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) {
            it?.let { galleryImageUri ->
                contentResolver.takePersistableUriPermission(
                    galleryImageUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                viewModel.onPhotoPicked(uri = galleryImageUri)
            }
        }

        viewModel.viewStateLiveData.observe(this) { picture ->
            Glide.with(this)
                .load(picture.uri)
                .into(binding.pictureImage)

            binding.uriErrorLbl.isVisible = picture.isUriErrorVisible

            binding.pictureDescriptionInput.setTextKeepState(picture.description)
            binding.pictureDescriptionInputLayout.error = picture.descriptionError
        }

        viewModel.photoEventLiveData.observe(this) { pictureEvent ->
            when (pictureEvent) {
                is PhotoEvent.ShowPickerDialog -> {
                    val pickerDialogItems = pictureEvent.items.map { it.toCharSequence(this) }.toTypedArray()
                    MaterialAlertDialogBuilder(this)
                        .setTitle(R.string.image_picker_dialog_title)
                        .setItems(pickerDialogItems) { _, which -> viewModel.onPickerDialogItemClicked(which) }
                        .show()
                }
                is PhotoEvent.OpenCamera -> {
                    cameraPictureUri = FileProvider.getUriForFile(
                        this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        File.createTempFile("tmp_camera_pic_", ".jpg")
                    )
                    cameraLauncher.launch(cameraPictureUri)
                }
                is PhotoEvent.OpenGallery -> galleryLauncher.launch(arrayOf("image/*"))
                is PhotoEvent.Exit -> finish()
            }
        }
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
            viewModel.onEditMenuItemClicked()
            true
        }
        else -> false
    }
}