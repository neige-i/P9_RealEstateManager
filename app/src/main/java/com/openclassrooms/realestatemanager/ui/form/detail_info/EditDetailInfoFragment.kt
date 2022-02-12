package com.openclassrooms.realestatemanager.ui.form.detail_info

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.openclassrooms.realestatemanager.BuildConfig
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentEditDetailInfoBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class EditDetailInfoFragment : Fragment() {

    private var mutableBinding: FragmentEditDetailInfoBinding? = null
    private val binding get() = mutableBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        mutableBinding = FragmentEditDetailInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel = ViewModelProvider(this).get(EditDetailInfoViewModel::class.java)

        enableEditTextScrolling()
        binding.detailInfoDescriptionInput.doAfterTextChanged {
            viewModel.onDescriptionChanged(it?.toString())
        }

        val photoAdapter = initPhotoAdapter(viewModel)
        binding.detailInfoPhotoList.adapter = photoAdapter

        viewModel.viewStateLiveData.observe(viewLifecycleOwner) {
            binding.detailInfoDescriptionInput.setText(it.description)

            photoAdapter.submitList(it.photoList)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun enableEditTextScrolling() {
        binding.detailInfoDescriptionInput.setOnTouchListener { v, event ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            if (event.action and MotionEvent.ACTION_MASK == MotionEvent.ACTION_UP) {
                v.parent.requestDisallowInterceptTouchEvent(false)
            }

            false
        }
    }

    private fun initPhotoAdapter(viewModel: EditDetailInfoViewModel): PhotoAdapter {
        var cameraPictureUri: Uri? = null

        val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
            viewModel.onPictureTaken(it)
        }
        val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) {
            viewModel.onPictureTaken(cameraPictureUri)
        }

        val imagePickerDialogItems = arrayOf(
            getString(R.string.image_picker_dialog_gallery_item),
            getString(R.string.image_picker_dialog_camera_item)
        )

        return PhotoAdapter {
            viewModel.onPhotoClicked(position = it)

            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.image_picker_dialog_title)
                .setItems(imagePickerDialogItems) { _, which ->
                    when (which) {
                        0 -> galleryLauncher.launch("image/*")
                        1 -> {
                            cameraPictureUri = getCameraPictureUri()
                            cameraLauncher.launch(cameraPictureUri)
                        }
                    }
                }
                .show()
        }
    }

    private fun getCameraPictureUri(): Uri = FileProvider.getUriForFile(
        requireContext(),
        BuildConfig.APPLICATION_ID + ".provider",
        File.createTempFile("tmp_camera_pic_", ".jpg")
    )


    override fun onDestroyView() {
        super.onDestroyView()
        mutableBinding = null
    }
}
