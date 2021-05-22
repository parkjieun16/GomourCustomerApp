package com.santaistiger.gomourcustomerapp.ui.customview

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.math.MathUtils.clamp
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.santaistiger.gomourcustomerapp.R
import com.santaistiger.gomourcustomerapp.databinding.DialogRoundedAlertBinding

class RoundedAlertDialog() : DialogFragment() {
    private lateinit var binding: DialogRoundedAlertBinding
    private lateinit var message: String
    private lateinit var positiveBtnProperty: DialogBtnProperty
    private lateinit var negativeBtnProperty: DialogBtnProperty

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.dialog_rounded_alert,
            null,
            false
        )


        binding.tvMessage.text = message
        binding.btnPositive.text = positiveBtnProperty.text
        binding.btnPositive.setOnClickListener(positiveBtnProperty.listener)
        binding.btnNegative.text = negativeBtnProperty.text
        binding.btnNegative.setOnClickListener(negativeBtnProperty.listener)

        return binding.root
    }


    override fun onStart() {
        super.onStart()
        val size = message.length * 14 + 50
        val width = clamp(size, 120, 300)

        dialog?.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(toDp(width), WindowManager.LayoutParams.WRAP_CONTENT)
        }
    }


    fun setMessage(text: String): RoundedAlertDialog {
        message = text
        return this
    }

    fun setPositiveButton(text: String, listener: (() -> Unit)?): RoundedAlertDialog {
        positiveBtnProperty = DialogBtnProperty(text) {
            listener?.invoke()
            dismiss()
        }
        return this
    }

    fun setNegativeButton(text: String, listener: (() -> Unit)?): RoundedAlertDialog {
        negativeBtnProperty = DialogBtnProperty(text) {
            listener?.invoke()
            dismiss()
        }
        return this
    }

    data class DialogBtnProperty(val text: String, val listener: View.OnClickListener?)

    private fun toDp(px: Int): Int {
        val metrics = resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px.toFloat(), metrics).toInt()
    }
}