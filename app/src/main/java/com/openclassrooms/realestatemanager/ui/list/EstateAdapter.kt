package com.openclassrooms.realestatemanager.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ItemEstateBinding
import com.openclassrooms.realestatemanager.ui.util.toCharSequence

class EstateAdapter : ListAdapter<EstateViewState, EstateAdapter.EstateViewHolder>(EstateDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = EstateViewHolder(
        ItemEstateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: EstateViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class EstateViewHolder(private val binding: ItemEstateBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(estate: EstateViewState) {
            Glide.with(binding.root)
                .load(estate.photoUrl)
                .error(R.drawable.ic_photo)
                .into(binding.estatePhotoImg)

            binding.estateTypeTxt.setText(estate.type)
            binding.estateCityTxt.text = estate.city
            binding.estatePriceTxt.text = estate.price.toCharSequence(binding.root.context)

            val estateStyle = estate.style

            binding.root.setBackgroundColor(getColor(estateStyle.backgroundColor))
            binding.estatePriceTxt.setTextColor(getColor(estateStyle.priceTextColor))
            binding.estateCityTxt.setTextColor(getColor(estateStyle.cityTextColor))

            binding.root.setOnClickListener { estate.onClicked() }
        }

        private fun getColor(@ColorRes colorId: Int): Int = ContextCompat.getColor(binding.root.context, colorId)
    }

    class EstateDiffUtil : DiffUtil.ItemCallback<EstateViewState>() {

        override fun areItemsTheSame(oldItem: EstateViewState, newItem: EstateViewState): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: EstateViewState, newItem: EstateViewState): Boolean {
            return oldItem == newItem
        }
    }
}