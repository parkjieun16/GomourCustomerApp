package com.santaistiger.gomourcustomerapp.data.model

/**
 * @param deliveryTime
 * 배달 완료 전 - 예상 도착 시간 / 배달 완료 후 - 도착시간
 */

data class Order(
        val orderId: String,
        val customerUid: String,
        val deliveryMan: String,
        val stores: List<Store>,
        val deliveryCharge: Int,
        val destination: Place,
        val message: String,
        val orderDate: Long,
        var deliveryTime: Long
)
