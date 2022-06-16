package com.openclassrooms.realestatemanager.ui.filter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.databinding.ItemCheckBinding

class CheckListAdapter : ListAdapter<MultiChoiceViewState.CheckItem, CheckListAdapter.CheckListViewHolder>(CheckListDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckListViewHolder {
        return CheckListViewHolder(ItemCheckBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: CheckListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    class CheckListViewHolder(private val binding: ItemCheckBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(checkItem: MultiChoiceViewState.CheckItem) {
            binding.root.apply {
                setText(checkItem.label)
                isChecked = checkItem.isChecked
                setOnClickListener { checkItem.onClicked(isChecked) }
            }
        }
    }

    class CheckListDiffUtil : DiffUtil.ItemCallback<MultiChoiceViewState.CheckItem>() {
        override fun areItemsTheSame(oldItem: MultiChoiceViewState.CheckItem, newItem: MultiChoiceViewState.CheckItem): Boolean {
            return oldItem.label == newItem.label
        }

        override fun areContentsTheSame(oldItem: MultiChoiceViewState.CheckItem, newItem: MultiChoiceViewState.CheckItem): Boolean {
            return oldItem == newItem
        }

    }
}