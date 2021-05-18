package com.santaistiger.gomourcustomerapp.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.*
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.santaistiger.gomourcustomerapp.data.model.Order
import com.santaistiger.gomourcustomerapp.data.model.Place
import com.santaistiger.gomourcustomerapp.data.model.Status
import com.santaistiger.gomourcustomerapp.data.model.Store
import com.santaistiger.gomourcustomerapp.ui.customview.*
import com.santaistiger.gomourcustomerapp.ui.doorder.StoreAdapter
import com.santaistiger.gomourcustomerapp.ui.orderdetail.OrderDetailStoreAdapater
import java.text.NumberFormat
import java.text.SimpleDateFormat


object BindingUtils {
    val numberFormat = NumberFormat.getInstance()

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

    @BindingAdapter("bind_detail_store_list")
    @JvmStatic
    fun bindDetailStoreList(recyclerView: RecyclerView, item: MutableLiveData<Order>) {
        if (item.value != null) {
            val order = item.value!!
            val stores = order.stores

            if (recyclerView.adapter == null) {
                recyclerView.layoutManager =
                    LinearLayoutManager(recyclerView.context)
                recyclerView.adapter = OrderDetailStoreAdapater()
            }
            (recyclerView.adapter as OrderDetailStoreAdapater).items = stores ?: ArrayList()
            recyclerView.adapter?.notifyDataSetChanged()
        }
    }

    @BindingAdapter("bind_detail_delivery_time")
    @JvmStatic
    fun bindDetailDeliveryTime(view: TextView, item: MutableLiveData<Order>) {
        if (item.value != null) {
            val order = item.value!!
            view.text = if (order.status == Status.DELIVERY_COMPLETE) {
                SimpleDateFormat("yyyy-MM-dd (EEE) hh:mm 배달 완료").format(order.deliveryTime)
            } else {
                SimpleDateFormat("yyyy-MM-dd (EEE) hh:mm 도착 예정").format(order.deliveryTime)
            }
        }
    }

    @BindingAdapter("bind_detail_destination")
    @JvmStatic
    fun bindDetailDestination(view: DetailDestinationView, item: MutableLiveData<Order>) {
        if (item.value != null) {
            val order = item.value!!
            view.binding.tvStoreAddress.text = order.destination?.getDisplayName()

            when (order.status) {
                Status.PICKUP_COMPLETE -> {
                    setComplete(view.binding.tvPickupComplete)
                }

                Status.DELIVERY_COMPLETE -> {
                    setComplete(view.binding.tvPickupComplete)
                    setComplete(view.binding.tvDeliveryComplete)
                }
            }
        }
    }

    private fun setComplete(tv: AppCompatTextView) {
        tv.text = tv.hint.toString()
    }


    @BindingAdapter("bind_detail_message")
    @JvmStatic
    fun bindDetailMessage(view: DetailMessageView, item: MutableLiveData<Order>) {
        if (item.value != null) {
            view.binding.tvMessage.text = item.value!!.message
        }
    }

    @BindingAdapter("bind_detail_price")
    @JvmStatic
    fun bindDetailPrice(view: DetailPriceView, item: MutableLiveData<Order>) {
        if (item.value != null) {
            val order = item.value!!
            var price = order.deliveryCharge ?: 0
            for (store in order.stores!!) {
                price += store.cost ?: 0
            }
            view.binding.tvPrice.text = numberFormat.format(price) + " 원"
        }
    }


    @BindingAdapter("bind_detail_cost")
    @JvmStatic
    fun bindDetailCost(view: AppCompatTextView, item: Int?) {
        if (item != null) {
            view.text = numberFormat.format(item)
        }
    }

}

