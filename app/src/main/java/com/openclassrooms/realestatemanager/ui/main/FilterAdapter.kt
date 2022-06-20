package com.openclassrooms.realestatemanager.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.databinding.ItemChipBinding
import com.openclassrooms.realestatemanager.ui.util.toCharSequence

class FilterAdapter : ListAdapter<FilterChipViewState, FilterAdapter.FilterViewHolder>(FilterDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = FilterViewHolder(
        ItemChipBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    class FilterViewHolder(private val binding: ItemChipBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(filterChip: FilterChipViewState) {
            binding.root.apply {
                isCheckable = false

                val filterChipStyle = filterChip.style

                text = filterChipStyle.text.toCharSequence(context)
                setChipBackgroundColorResource(filterChipStyle.backgroundColor)
                isCloseIconVisible = filterChipStyle.isCloseIconVisible

                setOnClickListener { filterChip.onClicked() }
                setOnCloseIconClickListener { filterChip.onCloseIconClicked() }
            }
        }
    }

    class FilterDiffUtil : DiffUtil.ItemCallback<FilterChipViewState>() {

        override fun areItemsTheSame(oldItem: FilterChipViewState, newItem: FilterChipViewState): Boolean {
            return oldItem.style.text == newItem.style.text
        }

        override fun areContentsTheSame(oldItem: FilterChipViewState, newItem: FilterChipViewState): Boolean {
            return oldItem == newItem
        }
    }
}