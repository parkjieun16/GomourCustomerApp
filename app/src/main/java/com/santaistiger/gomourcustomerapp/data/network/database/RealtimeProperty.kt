package com.santaistiger.gomourcustomerapp.data.network.database

import com.santaistiger.gomourcustomerapp.data.model.Order

data class OrderResponse(
    var order: Order? = null,
    var exception: Exception? = null
)