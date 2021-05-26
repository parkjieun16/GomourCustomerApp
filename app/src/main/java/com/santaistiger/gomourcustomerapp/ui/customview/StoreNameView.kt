package com.santaistiger.gomourcustomerapp.ui.customview

/**
 * Created by Jieun Park.
 */

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import com.santaistiger.gomourcustomerapp.R
import com.santaistiger.gomourcustomerapp.databinding.ItemListStoreBinding

class StoreNameView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    val binding: ItemListStoreBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.item_list_store,
        this,
        true
    )
}