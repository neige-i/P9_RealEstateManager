package com.openclassrooms.realestatemanager.ui.form.address

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.databinding.ItemChipBinding

class ChipAdapter(
    private val listener: (Int, Boolean) -> Unit,
) : ListAdapter<AddressViewState.ChipViewState, ChipAdapter.ChipViewHolder>(ChipDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChipViewHolder =
        ChipViewHolder(ItemChipBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ChipViewHolder, position: Int) {
        holder.bind(getItem(position), listener)
    }

    class ChipViewHolder(val binding: ItemChipBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            chipViewState: AddressViewState.ChipViewState,
            listener: (Int, Boolean) -> Unit,
        ) {
            binding.root.tag = chipViewState.labelId
            binding.root.setText(chipViewState.labelId)
            binding.root.isChecked = chipViewState.isSelected

            binding.root.setOnCheckedChangeListener { compoundButton, isChecked ->
                listener(compoundButton.tag as Int, isChecked)
            }
        }
    }

    class ChipDiffUtil : DiffUtil.ItemCallback<AddressViewState.ChipViewState>() {
        override fun areItemsTheSame(
            oldItem: AddressViewState.ChipViewState,
            newItem: AddressViewState.ChipViewState,
        ): Boolean {
            return oldItem.labelId == newItem.labelId
        }

        override fun areContentsTheSame(
            oldItem: AddressViewState.ChipViewState,
            newItem: AddressViewState.ChipViewState,
        ): Boolean {
            return oldItem == newItem
        }
    }
}