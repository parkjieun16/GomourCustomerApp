package com.santaistiger.gomourcustomerapp.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Store(
        var place: Place = Place(),
        var menu: String? = null,
        var cost: Int? = null
) : Parcelable
