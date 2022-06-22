package com.openclassrooms.realestatemanager.ui.form.detail_info

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentEditDetailInfoBinding
import com.openclassrooms.realestatemanager.ui.form.picker_dialog.PicturePickerDialog
import com.openclassrooms.realestatemanager.ui.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditDetailInfoFragment : Fragment(R.layout.fragment_edit_detail_info) {

    private val binding by viewBinding { FragmentEditDetailInfoBinding.bind(it) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel = ViewModelProvider(this).get(EditDetailInfoViewModel::class.java)

        enableEditTextScrolling()
        binding.detailInfoDescriptionInput.doAfterTextChanged { viewModel.onDescriptionChanged(it?.toString()) }

        val addPhotoAdapter = AddPhotoAdapter(object : AddPhotoAdapter.PhotoListener {
            override fun add(position: Int) {
                viewModel.onPhotoAdded(position)
                PicturePickerDialog().show(parentFragmentManager, null)
            }

            override fun open(position: Int, photo: DetailInfoViewState.PhotoViewState.Photo) {
                viewModel.onPhotoOpened(position, photo)
            }

            override fun remove(position: Int) {
                viewModel.onPhotoRemoved(position)
            }

        })
        binding.detailInfoPhotoList.adapter = addPhotoAdapter

        viewModel.viewStateLiveData.observe(viewLifecycleOwner) {
            binding.detailInfoDescriptionInput.setTextKeepState(it.description)
            addPhotoAdapter.submitList(it.photoList)
        }

        viewModel.showErrorEventLiveData.observe(viewLifecycleOwner) { errorMessageId ->
            val redColor = ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark)
            Snackbar.make(binding.root, errorMessageId, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(redColor)
                .show()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun enableEditTextScrolling() {
        binding.detailInfoDescriptionInput.setOnTouchListener { view, _ ->
            view.parent.requestDisallowInterceptTouchEvent(true)
            false
        }
    }
}
