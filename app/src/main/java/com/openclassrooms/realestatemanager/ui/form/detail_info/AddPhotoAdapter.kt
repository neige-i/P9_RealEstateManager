package com.openclassrooms.realestatemanager.ui.form.detail_info

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ItemAddPhotoBinding
import com.openclassrooms.realestatemanager.ui.form.detail_info.DetailInfoViewState.PhotoViewState.Add
import com.openclassrooms.realestatemanager.ui.form.detail_info.DetailInfoViewState.PhotoViewState.Photo

class AddPhotoAdapter : ListAdapter<DetailInfoViewState.PhotoViewState, AddPhotoAdapter.AddPhotoViewHolder>(PhotoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = AddPhotoViewHolder(
        ItemAddPhotoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: AddPhotoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AddPhotoViewHolder(private val binding: ItemAddPhotoBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(photo: DetailInfoViewState.PhotoViewState) {
            when (photo) {
                is Add -> {
                    binding.photoImg.setImageResource(R.drawable.ic_add_photo)
                    binding.photoImg.setPadding(
                        TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            50f,
                            binding.root.resources.displayMetrics
                        ).toInt()
                    )

                    binding.photoDescriptionTxt.isVisible = false
                    binding.photoDeleteImg.isVisible = false

                    binding.root.setOnClickListener { photo.onClicked() }
                }
                is Photo -> {
                    Glide.with(binding.root)
                        .load(photo.uri)
                        .error(R.drawable.ic_photo)
                        .into(binding.photoImg)

                    binding.photoDescriptionTxt.text = photo.description

                    binding.photoImg.setOnClickListener { photo.onClicked() }
                    binding.photoDeleteImg.setOnClickListener { photo.onDeleteButtonClicked() }
                }
            }
        }
    }

    class PhotoDiffCallback : DiffUtil.ItemCallback<DetailInfoViewState.PhotoViewState>() {

        override fun areItemsTheSame(oldItem: DetailInfoViewState.PhotoViewState, newItem: DetailInfoViewState.PhotoViewState): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: DetailInfoViewState.PhotoViewState, newItem: DetailInfoViewState.PhotoViewState): Boolean {
            return oldItem == newItem
        }
    }
}