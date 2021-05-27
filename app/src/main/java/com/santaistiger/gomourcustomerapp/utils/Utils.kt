/**
 * created by Kang Gumsil
 */
package com.santaistiger.gomourcustomerapp.utils

import android.util.DisplayMetrics
import android.util.TypedValue
import java.text.NumberFormat


val numberFormat = NumberFormat.getInstance()

fun toDp(metrics: DisplayMetrics, px: Int): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px.toFloat(), metrics).toInt()
}