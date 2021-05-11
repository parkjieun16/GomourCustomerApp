package com.santaistiger.gomourcustomerapp.ui.customview


import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.santaistiger.gomourcustomerapp.R
import com.santaistiger.gomourcustomerapp.databinding.ItemStoreBinding


class StoreView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    val binding: ItemStoreBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.item_store,
            this,
            true
    )
}