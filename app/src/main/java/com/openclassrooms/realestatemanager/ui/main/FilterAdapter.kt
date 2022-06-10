package com.openclassrooms.realestatemanager.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.databinding.ItemChipBinding
import com.openclassrooms.realestatemanager.ui.main.MainViewState.ChipViewState
import com.openclassrooms.realestatemanager.ui.util.toCharSequence

class FilterAdapter :
    ListAdapter<ChipViewState, FilterAdapter.FilterViewHolder>(FilterDiffUtil()) {

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

        fun bind(chip: ChipViewState) {
            binding.root.isCheckable = false

            val chipStyle = chip.style

            binding.root.text = chipStyle.text.toCharSequence(binding.root.context)

            binding.root.setChipBackgroundColorResource(chipStyle.backgroundColor)
            binding.root.isCloseIconVisible = chipStyle.isCloseIconVisible

            binding.root.setOnClickListener { chip.onFilterClicked() }
            binding.root.setOnCloseIconClickListener { chip.onCloseIconClicked() }
        }
    }

    class FilterDiffUtil : DiffUtil.ItemCallback<ChipViewState>() {

        override fun areItemsTheSame(oldItem: ChipViewState, newItem: ChipViewState): Boolean {
            return oldItem.style.text == newItem.style.text
        }

        override fun areContentsTheSame(
            oldItem: ChipViewState,
            newItem: ChipViewState
        ): Boolean {
            return oldItem == newItem
        }
    }
}