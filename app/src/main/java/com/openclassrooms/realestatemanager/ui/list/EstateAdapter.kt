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

class EstateAdapter(
    private val listener: (selectedEstateId: Long) -> Unit,
) : ListAdapter<EstateViewState, EstateAdapter.EstateViewHolder>(EstateDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = EstateViewHolder(
        ItemEstateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: EstateViewHolder, position: Int) {
        holder.bind(getItem(position), listener)
    }

    class EstateViewHolder(private val binding: ItemEstateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(estateViewState: EstateViewState, listener: (Long) -> Unit) {
            Glide.with(binding.root)
                .load(estateViewState.photoUrl)
                .error(R.drawable.ic_photo)
                .into(binding.estatePhotoImg)

            binding.estateTypeTxt.text = estateViewState.type
            binding.estateCityTxt.text = estateViewState.city
            binding.estatePriceTxt.text = estateViewState.price

            val estateStyle = estateViewState.style

            binding.root.setBackgroundColor(getColor(estateStyle.backgroundColor))
            binding.estatePriceTxt.setTextColor(getColor(estateStyle.priceTextColor))
            binding.estateCityTxt.setTextColor(getColor(estateStyle.cityTextColor))

            binding.root.setOnClickListener { listener.invoke(estateViewState.id) }
        }

        private fun getColor(@ColorRes colorId: Int): Int =
            ContextCompat.getColor(binding.root.context, colorId)
    }

    class EstateDiffUtil : DiffUtil.ItemCallback<EstateViewState>() {
        override fun areItemsTheSame(
            oldItem: EstateViewState,
            newItem: EstateViewState
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: EstateViewState,
            newItem: EstateViewState
        ): Boolean = oldItem == newItem
    }
}