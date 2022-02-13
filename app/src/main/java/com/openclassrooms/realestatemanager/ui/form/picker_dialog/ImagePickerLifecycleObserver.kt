package com.openclassrooms.realestatemanager.ui.form.picker_dialog

import android.content.Context
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.openclassrooms.realestatemanager.BuildConfig
import java.io.File

class ImagePickerLifecycleObserver(
    private val registry: ActivityResultRegistry,
    private val context: Context,
    private val listener: (Uri) -> Unit,
) : DefaultLifecycleObserver {

    private lateinit var galleryLauncher: ActivityResultLauncher<String>
    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>

    private var cameraPictureUri: Uri? = null

    override fun onCreate(owner: LifecycleOwner) {
        galleryLauncher =
            registry.register("gallery", owner, ActivityResultContracts.GetContent()) {
                listener(it)
            }
        cameraLauncher = registry.register("camera", owner, ActivityResultContracts.TakePicture()) {
            val capturedCameraPicturedUri = cameraPictureUri
            if (it && capturedCameraPicturedUri != null) {
                listener(capturedCameraPicturedUri)
            }
        }
    }

    fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    fun openCamera() {
        cameraPictureUri = getCameraPictureUri()
        cameraLauncher.launch(cameraPictureUri)
    }

    private fun getCameraPictureUri(): Uri = FileProvider.getUriForFile(
        context,
        BuildConfig.APPLICATION_ID + ".provider",
        File.createTempFile("tmp_camera_pic_", ".jpg")
    )
}