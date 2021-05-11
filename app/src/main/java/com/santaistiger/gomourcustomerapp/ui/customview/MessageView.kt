package com.santaistiger.gomourcustomerapp.ui.customview


import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.InverseBindingMethod
import com.santaistiger.gomourcustomerapp.R
import com.santaistiger.gomourcustomerapp.databinding.ItemMessageBinding


class MessageView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    val binding: ItemMessageBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.item_message,
            this,
            true
    )

    fun getMessage(): String = binding.etMessage.text.toString()
}