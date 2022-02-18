package com.openclassrooms.realestatemanager.ui.form.picture

import android.os.Bundle
import android.view.*
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentPictureBinding
import com.openclassrooms.realestatemanager.ui.form.picker_dialog.PicturePickerDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PictureFragment : Fragment() {

    private var mutableBinding: FragmentPictureBinding? = null
    private val binding: FragmentPictureBinding get() = mutableBinding!!
    private val viewModel: PictureViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        mutableBinding = FragmentPictureBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.pictureDescriptionInput.doAfterTextChanged {
            viewModel.onDescriptionChanged(it?.toString())
        }

        viewModel.viewStateLiveData.observe(viewLifecycleOwner) {
            Glide.with(this)
                .load(it.uri)
                .into(binding.pictureImage)

            binding.pictureDescriptionInput.setText(it.description)
            binding.pictureDescriptionInputLayout.error = it.descriptionError
        }

        viewModel.exitEventLiveData.observe(viewLifecycleOwner) {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_picture, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.save_picture -> {
            viewModel.onSaveMenuItemClicked()
            true
        }
        R.id.edit_picture -> {
            PicturePickerDialog().show(childFragmentManager, null)
            true
        }
        else -> false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mutableBinding = null
        viewModel.onFragmentViewDestroyed()
    }
}