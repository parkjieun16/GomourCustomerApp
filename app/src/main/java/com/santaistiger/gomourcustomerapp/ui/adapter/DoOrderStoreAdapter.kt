/**
 * created by Kang Gumsil
 */
package com.santaistiger.gomourcustomerapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.santaistiger.gomourcustomerapp.R
import com.santaistiger.gomourcustomerapp.data.model.Store
import com.santaistiger.gomourcustomerapp.databinding.ItemDoStoreBinding
import com.santaistiger.gomourcustomerapp.ui.view.DoOrderFragmentDirections

class DoOrderStoreAdapter : RecyclerView.Adapter<DoOrderStoreAdapter.ViewHolder>() {

    var items = ArrayList<Store>()

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // 첫번째 StoreView가 아니면 '-'버튼과 클릭 리스너 등록
        if (position != 0) {
            holder.viewBinding.ibDelItem.setImageResource(R.drawable.ic_del_btn)
            holder.viewBinding.ibDelItem.setOnClickListener { deleteItem(position) }
        }

        // textView에 클릭 리스너 등록
        holder.viewBinding.tvStoreAddress.setOnClickListener { searchPlace(it, position) }
        holder.bind(items[position])
    }

    private fun searchPlace(view: View, position: Int) {
        DoOrderFragmentDirections.actionDoOrderFragmentToSearchPlaceFragment().let {
            it.position = position
            view.findNavController().navigate(it)
        }
    }

    private fun deleteItem(position: Int) {
        items.removeAt(position)
    }

    fun addItem(item: Store = Store()) {
        items.add(item)
    }

    class ViewHolder private constructor(val viewBinding: ItemDoStoreBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {

        fun bind(item: Store) {
            viewBinding.item = item
            viewBinding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemDoStoreBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

