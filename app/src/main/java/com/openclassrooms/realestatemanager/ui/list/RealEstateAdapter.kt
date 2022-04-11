package com.openclassrooms.realestatemanager.ui.list

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ItemRealEstateBinding

class RealEstateAdapter(
    private val listener: (Long) -> Unit,
) : ListAdapter<RealEstateViewState, RealEstateAdapter.SimpleViewHolder>(SimpleDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SimpleViewHolder(
        ItemRealEstateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: SimpleViewHolder, position: Int) {
        holder.bind(getItem(position), listener)
    }

    class SimpleViewHolder(private val binding: ItemRealEstateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: RealEstateViewState, listener: (Long) -> Unit) {
            Glide
                .with(binding.root)
                .load(item.photoUrl)
                .error(R.drawable.ic_photo)
                .into(binding.realEstatePhoto)

            binding.realEstateType.text = item.type
            binding.realEstateCity.text = item.city
            binding.realEstatePrice.text = item.price

            binding.root.setBackgroundColor(getColor(item.backgroundColor))
            binding.realEstatePrice.setTextColor(getColor(item.priceTextColor))

            binding.root.setOnClickListener { listener(item.id) }
        }

        private fun getColor(@ColorRes colorId: Int): Int =
            ContextCompat.getColor(binding.root.context, colorId)
    }

    class SimpleDiffUtil : DiffUtil.ItemCallback<RealEstateViewState>() {
        override fun areItemsTheSame(
            oldItem: RealEstateViewState,
            newItem: RealEstateViewState
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: RealEstateViewState,
            newItem: RealEstateViewState
        ): Boolean = oldItem == newItem
    }
}