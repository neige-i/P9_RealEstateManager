package com.openclassrooms.realestatemanager.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.databinding.ItemSimpleBinding

class SimpleAdapter(
    private val listener: (String) -> Unit,
) : ListAdapter<String, SimpleAdapter.SimpleViewHolder>(SimpleDiffUtil()) {

    class SimpleViewHolder(private val binding: ItemSimpleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: String, listener: (String) -> Unit) {
            binding.item.text = item
            binding.item.setOnClickListener { listener(item) }
        }
    }

    class SimpleDiffUtil : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String) = oldItem == newItem

        override fun areContentsTheSame(oldItem: String, newItem: String) = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SimpleViewHolder(
        ItemSimpleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: SimpleViewHolder, position: Int) {
        holder.bind(getItem(position), listener)
    }
}