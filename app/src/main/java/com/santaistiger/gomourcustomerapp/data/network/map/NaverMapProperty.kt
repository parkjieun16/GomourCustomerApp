package com.santaistiger.gomourcustomerapp.data.network.map

import com.squareup.moshi.Json

/**
 * 최상위 프로퍼티
 */
data class NaverMapProperty(
    val message: String,
    val currentDateTime: String,
    val route: Map<String, List<Route>>,

    @Json(name = "code")
    val resultCode: Double
)

fun NaverMapProperty.getDistance() =
    route["traoptimal"]?.get(0)?.summary?.distance?.toInt()


data class Route(
    val summary: Summary,
    val path: List<List<Double>>
)

data class Summary(
    val start: Position,
    val goal: Position,
    val distance: Double
)

data class Position(
    val location: List<Double>
)