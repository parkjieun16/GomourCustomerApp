package com.santaistiger.gomourcustomerapp.data.network.map

import com.santaistiger.gomourcustomerapp.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

private const val BASE_URL = "https://naveropenapi.apigw.ntruss.com/"
private const val API_ID = BuildConfig.naverMapsApiId
private const val API_KEY = BuildConfig.naverMapsApiKey

/**
 * Build the Moshi object that Retrofit will be using, making sure to add the Kotlin adapter for
 * full Kotlin compatibility.
 */
private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

/**
 * Use the Retrofit builder to build a retrofit object using a Moshi converter with our Moshi
 * object.
 */
private val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(BASE_URL)
        .build()

/** A public interface that exposes the [getDirections] method */
interface NaverMapApiService {

    /**
     * Returns a Coroutine [NaverMapProperty] which can be fetched in a Coroutine scope.
     * The @GET annotation indicates that the "directions" endpoint will be requested with the GET
     * HTTP method
     */
    @Headers("X-NCP-APIGW-API-KEY-ID:$API_ID", "X-NCP-APIGW-API-KEY:$API_KEY")
    @GET("map-direction/v1/driving")
    suspend fun getDirections(@Query("start") start: String,
                              @Query("goal") goal: String,
                              @Query("waypoints") waypoints: String?
    ): NaverMapProperty
}

/** A public Api object that exposes the lazy-initialized Retrofit service */
object NaverMapApi {
    val retrofitService : NaverMapApiService by lazy { retrofit.create(NaverMapApiService::class.java) }
}