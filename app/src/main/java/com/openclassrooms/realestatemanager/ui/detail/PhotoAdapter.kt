package com.openclassrooms.realestatemanager.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ItemPhotoBinding

class PhotoAdapter(
    private val listener: (selectedPhoto: DetailViewState.WithInfo.Photo) -> Unit,
) : ListAdapter<DetailViewState.WithInfo.Photo, PhotoAdapter.PhotoViewHolder>(PhotoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PhotoViewHolder(
        ItemPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(getItem(position), listener)
    }

    class PhotoViewHolder(private val binding: ItemPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            photoViewState: DetailViewState.WithInfo.Photo,
            listener: (DetailViewState.WithInfo.Photo) -> Unit,
        ) {
            Glide.with(binding.root)
                .load(photoViewState.url)
                .error(R.drawable.ic_photo)
                .into(binding.mediaImg)

            binding.mediaDescriptionTxt.text = photoViewState.description

            binding.root.setOnClickListener { listener.invoke(photoViewState) }
        }
    }

    class PhotoDiffCallback : DiffUtil.ItemCallback<DetailViewState.WithInfo.Photo>() {

        override fun areItemsTheSame(
            oldItem: DetailViewState.WithInfo.Photo,
            newItem: DetailViewState.WithInfo.Photo
        ): Boolean = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: DetailViewState.WithInfo.Photo,
            newItem: DetailViewState.WithInfo.Photo
        ): Boolean = oldItem == newItem
    }
}