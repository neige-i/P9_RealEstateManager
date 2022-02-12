package com.openclassrooms.realestatemanager.ui.form.detail_info

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ItemAddPhotoBinding

class PhotoAdapter(
    private val addListener: (Int) -> Unit,
) : ListAdapter<DetailInfoViewState.PhotoViewState, RecyclerView.ViewHolder>(PhotoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PhotoViewHolder(
            ItemAddPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PhotoViewHolder -> holder.bind(getItem(position), addListener)
        }
    }

    class PhotoViewHolder(private val binding: ItemAddPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(viewState: DetailInfoViewState.PhotoViewState, addListener: (Int) -> Unit) {
            Glide.with(binding.root)
                .load((viewState as DetailInfoViewState.PhotoViewState.Add).pictureUri)
                .error(R.drawable.ic_add_photo)
                .into(binding.root)

            binding.root.setOnClickListener { addListener(adapterPosition) }
        }
    }

    class PhotoDiffCallback : DiffUtil.ItemCallback<DetailInfoViewState.PhotoViewState>() {

        override fun areItemsTheSame(
            oldItem: DetailInfoViewState.PhotoViewState,
            newItem: DetailInfoViewState.PhotoViewState,
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: DetailInfoViewState.PhotoViewState,
            newItem: DetailInfoViewState.PhotoViewState,
        ): Boolean {
            return oldItem == newItem
        }
    }
}