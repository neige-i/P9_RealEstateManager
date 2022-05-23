package com.openclassrooms.realestatemanager.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.databinding.ItemChipBinding
import com.openclassrooms.realestatemanager.ui.main.MainViewState.FilterViewState

class FilterAdapter :
    ListAdapter<FilterViewState, FilterAdapter.FilterViewHolder>(FilterDiffUtil()) {

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


    class FilterViewHolder(private val binding: ItemChipBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(filterViewState: FilterViewState) {
            binding.root.isCheckable = false

            binding.root.text = filterViewState.text

            binding.root.setChipBackgroundColorResource(filterViewState.backgroundColor)
            binding.root.isCloseIconVisible = filterViewState.isCloseIconVisible

            binding.root.setOnClickListener { filterViewState.onFilterClicked() }
            binding.root.setOnCloseIconClickListener { filterViewState.onCloseIconClicked() }
        }
    }

    class FilterDiffUtil : DiffUtil.ItemCallback<FilterViewState>() {

        override fun areItemsTheSame(oldItem: FilterViewState, newItem: FilterViewState): Boolean {
            return oldItem.text == newItem.text
        }

        override fun areContentsTheSame(
            oldItem: FilterViewState,
            newItem: FilterViewState
        ): Boolean {
            return oldItem == newItem
        }
    }
}