package com.openclassrooms.realestatemanager.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ItemMediaBinding

class MediaAdapter(
    private val listener: (DetailViewState.Info.Photo) -> Unit,
) : ListAdapter<DetailViewState.Info.Photo, MediaAdapter.MediaViewHolder>(MediaDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MediaViewHolder(
        ItemMediaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        holder.bind(getItem(position), listener)
    }

    class MediaViewHolder(private val binding: ItemMediaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            viewState: DetailViewState.Info.Photo,
            listener: (DetailViewState.Info.Photo) -> Unit,
        ) {
            Glide.with(binding.root)
                .load(viewState.url)
                .error(R.drawable.ic_add_photo)
                .into(binding.mediaImg)

            binding.mediaDescriptionTxt.text = viewState.description

            binding.root.setOnClickListener { listener.invoke(viewState) }
        }
    }

    class MediaDiffCallback : DiffUtil.ItemCallback<DetailViewState.Info.Photo>() {

        override fun areItemsTheSame(
            oldItem: DetailViewState.Info.Photo,
            newItem: DetailViewState.Info.Photo
        ): Boolean = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: DetailViewState.Info.Photo,
            newItem: DetailViewState.Info.Photo
        ): Boolean = oldItem == newItem
    }
}