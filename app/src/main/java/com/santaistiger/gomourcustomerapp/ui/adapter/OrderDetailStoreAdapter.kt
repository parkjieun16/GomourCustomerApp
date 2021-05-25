package com.santaistiger.gomourcustomerapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.santaistiger.gomourcustomerapp.data.model.Store
import com.santaistiger.gomourcustomerapp.databinding.ItemDetailStoreBinding

class OrderDetailStoreAdapter : RecyclerView.Adapter<OrderDetailStoreAdapter.ViewHolder>() {
    companion object {
        private const val TAG = "OrderDetailStoreAdapter"
    }
    var items = ArrayList<Store>()

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    class ViewHolder private constructor(private val viewBinding: ItemDetailStoreBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {

        fun bind(item: Store) {
            viewBinding.item = item
            viewBinding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemDetailStoreBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

