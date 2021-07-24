package com.santaistiger.gomourcustomerapp.data.network.map
import com.santaistiger.gomourcustomerapp.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

private const val BASE_URL = "https://dapi.kakao.com/"
private const val REST_API_KEY = BuildConfig.kakaoRESTApiKey

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


/** A public interface that exposes the [searchPlaces] method */
interface KakaoMapApiService {

    /**
     * Returns a Coroutine [KakaoMapProperty] which can be fetched in a Coroutine scope.
     * The @GET annotation indicates that the "place" endpoint will be requested with the GET
     * HTTP method
     */
    @Headers("Authorization: KakaoAK $REST_API_KEY")
    @GET("/v2/local/search/keyword.json")
    suspend fun searchPlaces(@Query("query") searchName: String,
                             @Query("x") longitude: Double,
                             @Query("y") latitude: Double,
                             @Query("page") page: String = "1",
                             @Query("sort") sortBy: String = "distance",
                             @Query("size") size: String = "15"): KakaoMapProperty
}

/** A public Api object that exposes the lazy-initialized Retrofit service */
object KakaoMapApi {
    val retrofitService : KakaoMapApiService by lazy { retrofit.create(KakaoMapApiService::class.java) }
}