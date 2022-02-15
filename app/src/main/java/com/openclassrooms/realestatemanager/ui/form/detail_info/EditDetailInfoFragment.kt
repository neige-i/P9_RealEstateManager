package com.openclassrooms.realestatemanager.ui.form.detail_info

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.openclassrooms.realestatemanager.databinding.FragmentEditDetailInfoBinding
import com.openclassrooms.realestatemanager.ui.form.picker_dialog.ImagePickerDialog
import com.openclassrooms.realestatemanager.ui.form.picker_dialog.ImagePickerLifecycleObserver
import dagger.hilt.android.AndroidEntryPoint

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

        val imagePickerLifecycleObserver = ImagePickerLifecycleObserver(
            registry = requireActivity().activityResultRegistry,
            context = requireContext()
        ) { viewModel.onPhotoPicked(it) }
        lifecycle.addObserver(imagePickerLifecycleObserver)

        enableEditTextScrolling()
        binding.detailInfoDescriptionInput.doAfterTextChanged {
            viewModel.onDescriptionChanged(it?.toString())
        }

        val photoAdapter = PhotoAdapter(object : PhotoAdapter.PhotoListener {
            override fun add(position: Int) {
                viewModel.onPhotoAdded(position)
                ImagePickerDialog(imagePickerLifecycleObserver).show(childFragmentManager, null)
            }

            override fun open(position: Int, picture: DetailInfoViewState.PhotoViewState.Picture) {
                viewModel.onPhotoOpened(position, picture)
            }

            override fun remove(position: Int) {
                viewModel.onPhotoRemoved(position)
            }

        })
        binding.detailInfoPhotoList.adapter = photoAdapter

        viewModel.viewStateLiveData.observe(viewLifecycleOwner) {
            binding.detailInfoDescriptionInput.setText(it.description)
            photoAdapter.submitList(it.photoList)
        }

        viewModel.showErrorEventLiveData.observe(viewLifecycleOwner) {
            val redColor = ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark)
            Snackbar
                .make(binding.root, it, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(redColor)
                .show()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun enableEditTextScrolling() {
        binding.detailInfoDescriptionInput.setOnTouchListener { v, _ ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            false
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        mutableBinding = null
    }
}
