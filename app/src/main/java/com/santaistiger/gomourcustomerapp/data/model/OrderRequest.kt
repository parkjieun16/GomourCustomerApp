package com.santaistiger.gomourcustomerapp.data.model

/**
 * 주문 요청에 대한 정보를 갖는 데이터 클래스
 *
 * @property orderId 각 주문요청을 식별하는 값. 현재시간(long)에 주문자의 uid가 더해진 형태
 *                    이후에 Order 클래스에서의 orderId와 같은 값을 갖는다.
 * @property customer 주문자에 대한 정보
 * @property stores 각 가게에 대한 주소와 요청사항
 * @property deliveryCharge 배달료
 * @property destination 배달 장소 주소
 * @property message 배송 메시지
 * @property orderDate 주문 시간. 주문 요청 객체가 생성된 시간
 */
data class OrderRequest(
        val customerUid: String,
        val stores: ArrayList<Store>? = null,
        val deliveryCharge: Int? = null,
        val destination: Place,
        val message: String? = null,
        val orderDate: Long = System.currentTimeMillis(),
        val orderId: String = "$orderDate$customerUid"
)
