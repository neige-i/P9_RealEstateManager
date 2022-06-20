package com.openclassrooms.realestatemanager.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ItemPhotoBinding

class PhotoAdapter : ListAdapter<PhotoViewState, PhotoAdapter.PhotoViewHolder>(PhotoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PhotoViewHolder(
        ItemPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PhotoViewHolder(private val binding: ItemPhotoBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(photo: PhotoViewState) {
            Glide.with(binding.root)
                .load(photo.url)
                .error(R.drawable.ic_photo)
                .into(binding.mediaImg)

            binding.mediaDescriptionTxt.text = photo.description
        }
    }

    class PhotoDiffCallback : DiffUtil.ItemCallback<PhotoViewState>() {

        override fun areItemsTheSame(oldItem: PhotoViewState, newItem: PhotoViewState): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: PhotoViewState, newItem: PhotoViewState): Boolean {
            return oldItem == newItem
        }
    }
}