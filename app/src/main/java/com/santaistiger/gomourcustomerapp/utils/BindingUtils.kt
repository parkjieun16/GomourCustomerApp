package com.santaistiger.gomourcustomerapp.utils

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.santaistiger.gomourcustomerapp.data.model.Place
import com.santaistiger.gomourcustomerapp.data.model.Store
import com.santaistiger.gomourcustomerapp.ui.customview.MessageView
import com.santaistiger.gomourcustomerapp.ui.customview.DestinationView
import com.santaistiger.gomourcustomerapp.ui.customview.PriceView
import com.santaistiger.gomourcustomerapp.ui.doorder.StoreAdapter
import kotlinx.android.synthetic.main.item_message.view.*

object BindingUtils {
    @BindingAdapter("bind_store_list")
    @JvmStatic fun bindStoreList(recyclerView: RecyclerView, items: ObservableArrayList<Store>) {
        if (recyclerView.adapter == null) {
            recyclerView.layoutManager =
                    LinearLayoutManager(recyclerView.context)
            recyclerView.adapter = StoreAdapter()
        }
        (recyclerView.adapter as StoreAdapter).items = items
        recyclerView.adapter?.notifyDataSetChanged()
    }

    @BindingAdapter("bind_destination")
    @JvmStatic fun setDestination(view: DestinationView, item: ObservableParcelable<Place>) {
        if (view.binding.item != item.get()) {
            view.binding.item = item.get()
        }
    }

    @BindingAdapter("bind_message")
    @JvmStatic fun setMessage(view: MessageView, item: ObservableField<String>) {
        if (view.binding.message != item.get()) {
            view.binding.message = item.get()
        }
    }

    @InverseBindingAdapter(attribute = "bind_message", event = "bind_messageAttrChanged")
    @JvmStatic fun getMessage(view: MessageView) : String {
        return view.getMessage()
    }

    @BindingAdapter("bind_messageAttrChanged")
    @JvmStatic fun setListener(view: MessageView, listener: InverseBindingListener?) {
        view.binding.etMessage.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { listener?.onChange() }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    @BindingAdapter("bind_price")
    @JvmStatic fun setPrice(view: PriceView, item: ObservableInt) {
        if (view.binding.price != item.get()) {
            view.binding.price = item.get()
        }
    }

}

