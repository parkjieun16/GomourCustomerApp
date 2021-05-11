package com.santaistiger.gomourcustomerapp.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import net.daum.mf.map.api.MapPoint

@Parcelize
data class Place(
     val placeName: String? = null,         // 가게명
     val latitude: Double? = null,
     val longitude: Double? = null,
     val roadAddressName: String? = null,    // 도로명 주소
) : Parcelable {

    fun getDisplayName(): String? {
        if (!roadAddressName.isNullOrBlank() && !placeName.isNullOrBlank()) {
            return "$roadAddressName ($placeName)"
        }
        return null
    }
}

