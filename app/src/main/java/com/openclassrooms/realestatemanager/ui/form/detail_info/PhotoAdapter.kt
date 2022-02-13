package com.openclassrooms.realestatemanager.ui.form.detail_info

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ItemAddPhotoBinding
import com.openclassrooms.realestatemanager.databinding.ItemPictureBinding

class PhotoAdapter(
    private val photoListener: PhotoListener,
) : ListAdapter<DetailInfoViewState.PhotoViewState, PhotoAdapter.SealedViewHolder>(PhotoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SealedViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (ViewType.values()[viewType]) {
            ViewType.PHOTO -> PhotoViewHolder(ItemPictureBinding.inflate(inflater, parent, false))
            ViewType.ADD -> AddViewHolder(ItemAddPhotoBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: SealedViewHolder, position: Int) {
        when (holder) {
            is AddViewHolder -> holder.bind(photoListener)
            is PhotoViewHolder -> holder.bind(
                getItem(position) as DetailInfoViewState.PhotoViewState.Picture,
                photoListener
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DetailInfoViewState.PhotoViewState.Add -> ViewType.ADD.ordinal
            is DetailInfoViewState.PhotoViewState.Picture -> ViewType.PHOTO.ordinal
        }
    }

    enum class ViewType {
        PHOTO,
        ADD,
    }

    sealed class SealedViewHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root)

    class PhotoViewHolder(private val binding: ItemPictureBinding) : SealedViewHolder(binding) {

        fun bind(
            viewState: DetailInfoViewState.PhotoViewState.Picture,
            photoListener: PhotoListener,
        ) {
            Glide.with(binding.root)
                .load(viewState.uri)
                .error(R.drawable.ic_add_photo)
                .into(binding.photoImage)

            binding.photoDescription.text = viewState.description

            binding.photoImage.setOnClickListener { photoListener.open(adapterPosition, viewState) }
            binding.photoDeleteImage.setOnClickListener { photoListener.remove(adapterPosition) }
        }
    }

    class AddViewHolder(private val binding: ItemAddPhotoBinding) : SealedViewHolder(binding) {

        fun bind(photoListener: PhotoListener) {
            binding.root.setOnClickListener { photoListener.add(adapterPosition) }
        }
    }

    class PhotoDiffCallback : DiffUtil.ItemCallback<DetailInfoViewState.PhotoViewState>() {

        override fun areItemsTheSame(
            oldItem: DetailInfoViewState.PhotoViewState,
            newItem: DetailInfoViewState.PhotoViewState,
        ): Boolean = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: DetailInfoViewState.PhotoViewState,
            newItem: DetailInfoViewState.PhotoViewState,
        ): Boolean = oldItem == newItem
    }

    interface PhotoListener {

        fun add(position: Int)
        fun open(position: Int, picture: DetailInfoViewState.PhotoViewState.Picture)
        fun remove(position: Int)
    }
}