/**
 * created by Kang Gumsil
 */
package com.santaistiger.gomourcustomerapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class OrderDetailViewModelFactory (private val param: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(OrderDetailViewModel::class.java)) {
            OrderDetailViewModel(param) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}