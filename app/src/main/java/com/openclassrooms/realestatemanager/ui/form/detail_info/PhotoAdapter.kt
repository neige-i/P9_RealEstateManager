package com.openclassrooms.realestatemanager.ui.form.detail_info

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.databinding.ItemAddPhotoBinding

class PhotoAdapter(
    private val addListener: () -> Unit,
) : ListAdapter<PhotoViewState, RecyclerView.ViewHolder>(PhotoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PhotoViewHolder(
            ItemAddPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PhotoViewHolder -> holder.bind(addListener)
        }
    }

    class PhotoViewHolder(private val binding: ItemAddPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(addListener: () -> Unit) {
            binding.root.setOnClickListener { addListener() }
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