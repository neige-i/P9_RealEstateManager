package com.openclassrooms.realestatemanager.ui.filter.checklist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.databinding.ItemCheckBinding

class CheckListAdapter : ListAdapter<CheckListViewState.CheckItem, CheckListAdapter.CheckListViewHolder>(CheckListDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckListViewHolder {
        return CheckListViewHolder(ItemCheckBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: CheckListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CheckListViewHolder(private val binding: ItemCheckBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(checkItem: CheckListViewState.CheckItem) {
            binding.root.apply {
                setText(checkItem.label)
                isChecked = checkItem.isChecked
                setOnClickListener { checkItem.onClicked(isChecked) }
            }
        }
    }

    class CheckListDiffUtil : DiffUtil.ItemCallback<CheckListViewState.CheckItem>() {

        override fun areItemsTheSame(oldItem: CheckListViewState.CheckItem, newItem: CheckListViewState.CheckItem): Boolean {
            return oldItem.label == newItem.label
        }

        override fun areContentsTheSame(oldItem: CheckListViewState.CheckItem, newItem: CheckListViewState.CheckItem): Boolean {
            return oldItem == newItem
        }
    }
}