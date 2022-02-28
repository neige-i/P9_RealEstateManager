package com.openclassrooms.realestatemanager.ui.form.image_launcher

import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.openclassrooms.realestatemanager.BuildConfig
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
abstract class ImageLauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: ImageLauncherViewModel by viewModels()

        var cameraPictureUri: Uri? = null

        val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) {
            viewModel.onPhotoPicked(uri = cameraPictureUri, success = it)
        }
        val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
            viewModel.onPhotoPicked(uri = it)
        }

        viewModel.imageLauncherEventLiveData.observe(this) {
            when (it) {
                ImageLauncherEvent.OpenCamera -> {
                    cameraPictureUri = getCameraPictureUri()
                    cameraLauncher.launch(cameraPictureUri)
                }
                ImageLauncherEvent.OpenGallery -> galleryLauncher.launch("image/*")
            }
        }
    }

    private fun getCameraPictureUri(): Uri = FileProvider.getUriForFile(
        this,
        BuildConfig.APPLICATION_ID + ".provider",
        File.createTempFile("tmp_camera_pic_", ".jpg")
    )
}