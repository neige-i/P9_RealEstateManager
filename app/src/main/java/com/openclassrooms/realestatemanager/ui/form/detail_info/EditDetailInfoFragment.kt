package com.openclassrooms.realestatemanager.ui.form.detail_info

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.openclassrooms.realestatemanager.databinding.FragmentEditDetailInfoBinding
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

        // noinspection ClickableViewAccessibility
        binding.detailInfoDescriptionInput.setOnTouchListener { v, event ->
            // Intercept touch event to enable scrolling through EditText content
            v.parent.requestDisallowInterceptTouchEvent(true)
            if (event.action and MotionEvent.ACTION_MASK == MotionEvent.ACTION_UP) {
                v.parent.requestDisallowInterceptTouchEvent(false)
            }
            return@setOnTouchListener false
        }
        binding.detailInfoDescriptionInput.doAfterTextChanged {
            viewModel.onDescriptionChanged(it?.toString())
        }

        binding.detailInfoPhotoList.adapter = PhotoAdapter {
            Log.d("Neige", "photo item listener: clicked")
        }.apply {
            submitList(List(9) { PhotoViewState.Add })
        }

        viewModel.viewStateLiveData.observe(viewLifecycleOwner) {
            binding.detailInfoDescriptionInput.setText(it.description)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mutableBinding = null
    }
}
