package com.santaistiger.gomourcustomerapp.data.network.map

import com.santaistiger.gomourcustomerapp.data.model.Place
import com.squareup.moshi.Json

/**
 * 최상위 property
 */
data class KakaoMapProperty(
    val meta: Meta,
    val documents: List<NetworkPlace>
)

/**
 * Convert Network results to domain objects
 */
fun List<NetworkPlace>.asDomainModel(): List<Place> {
    return this.map {
        val roadAddressName = when (it.roadAddressName) {
            "" -> it.addressName
            else -> it.roadAddressName
        }

        Place(
            placeName = it.placeName,
            latitude = it.latitude.toDouble(),
            longitude = it.longitude.toDouble(),
            roadAddressName = roadAddressName
        )
    }
}

/**
 * documents 안의 property
 */
data class NetworkPlace(
    val id: String,
    val distance: String,
    val phone: String,

    @Json(name = "place_name")  // 장소명(가게명)
    val placeName: String,

    @Json(name = "x")
    val longitude: String,

    @Json(name = "y")
    val latitude: String,

    @Json(name = "road_address_name")  // 도로명 주소
    val roadAddressName: String,

    @Json(name = "address_name")  // 구주소
    val addressName: String,

    @Json(name = "category_group_code")  // 카테고리 코드
    val categoryGroupCode: String,

    @Json(name = "category_group_name")  // 카테고리 명
    val categoryGroupName: String,

    @Json(name = "place_url")  // 카카오맵에서의 url (상세 페이지)
    val placeUrl: String
)

/**
 * Respond에 대한 메타 데이터
 */
data class Meta(
    @Json(name = "total_count")
    val totalCount: Double,

    @Json(name = "pageable_count")
    val pageCount: Double,

    @Json(name = "is_end")
    val isEnd: Boolean,

    @Json(name = "same_name")
    val sameName: RegionInfo
)

/**
 * 질의어의 지역 및 키워드 분석 정보
 */
data class RegionInfo(
    val region: List<String>,
    val keyword: String,

    @Json(name = "selected_region")
    val selectedRegion: String
)





