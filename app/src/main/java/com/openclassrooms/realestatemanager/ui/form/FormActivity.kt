package com.openclassrooms.realestatemanager.ui.form

import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.openclassrooms.realestatemanager.BuildConfig
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityFormBinding
import com.openclassrooms.realestatemanager.ui.form.pager.FormPagerFragment
import com.openclassrooms.realestatemanager.ui.form.picture.PictureFragment
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class FormActivity : AppCompatActivity() {

    private val viewModel: FormViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityFormBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.formToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add<FormPagerFragment>(binding.formContainer.id)
        }
        supportFragmentManager.addOnBackStackChangedListener {
            viewModel.onBackStackChanged(supportFragmentManager.backStackEntryCount)
        }

        var cameraPictureUri: Uri? = null

        val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
            viewModel.onPhotoPicked(uri = it)
        }
        val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) {
            viewModel.onPhotoPicked(uri = cameraPictureUri, success = it)
        }

        viewModel.viewStateLiveData.observe(this) { binding.formToolbar.title = it }

        viewModel.formEventLiveData.observe(this) {
            when (it) {
                FormEvent.ExitActivity -> finish()
                FormEvent.ExitFragment -> supportFragmentManager.popBackStack()
                is FormEvent.ShowDialog -> MaterialAlertDialogBuilder(this)
                    .setTitle(it.title)
                    .setMessage(it.message)
                    .setPositiveButton(it.positiveButtonText) { _, _ ->
                        viewModel.onDialogPositiveButtonClicked()
                    }
                    .setNegativeButton(it.negativeButtonText) { _, _ ->
                        viewModel.onDialogNegativeButtonClicked()
                    }
                    .show()
                FormEvent.ShowPicture -> supportFragmentManager.commit {
                    replace<PictureFragment>(binding.formContainer.id)
                    setReorderingAllowed(true)
                    addToBackStack(null)
                }
                FormEvent.OpenGallery -> galleryLauncher.launch("image/*")
                FormEvent.OpenCamera -> {
                    cameraPictureUri = getCameraPictureUri()
                    cameraLauncher.launch(cameraPictureUri)
                }
            }
        }
    }

    private fun getCameraPictureUri(): Uri = FileProvider.getUriForFile(
        this,
        BuildConfig.APPLICATION_ID + ".provider",
        File.createTempFile("tmp_camera_pic_", ".jpg")
    )

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_form, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.close_form)?.isVisible = supportFragmentManager.backStackEntryCount == 0
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            onBackPressed()
            true
        }
        R.id.close_form -> {
            viewModel.onCloseMenuItemClicked()
            true
        }
        else -> false
    }

    override fun onBackPressed() {
        viewModel.onGoBack(supportFragmentManager.backStackEntryCount)
    }
}