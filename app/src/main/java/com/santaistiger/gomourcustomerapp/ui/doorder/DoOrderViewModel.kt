package com.santaistiger.gomourcustomerapp.ui.doorder

import android.util.Log
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.databinding.ObservableParcelable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.santaistiger.gomourcustomerapp.data.model.OrderRequest
import com.santaistiger.gomourcustomerapp.data.model.Place
import com.santaistiger.gomourcustomerapp.data.model.Store
import com.santaistiger.gomourcustomerapp.data.repository.Repository
import com.santaistiger.gomourcustomerapp.data.repository.RepositoryImpl
import kotlinx.coroutines.launch
import java.lang.Exception
import kotlin.math.roundToInt

private const val TAG = "DoOrderViewModel"
private const val tmpUid = "a1s2d3f4"

class DoOrderViewModel : ViewModel() {
    val storeList = ObservableArrayList<Store>()
    val destination = ObservableParcelable<Place>()
    val message = ObservableField<String>()
    val price = ObservableInt()
    val orderRequest = MutableLiveData<OrderRequest>()

    private val repository: Repository = RepositoryImpl()

    init {
        addStore()
        destination.set(Place())
    }

    fun addStore() {
        storeList.add(Store())
    }

    fun doneNavigateWaitMatch() {
        orderRequest.value = null
        storeList.clear()
        addStore()
        destination.set(Place())
        message.set(null)
        price.set(0)
    }

    /**
     * 주문하기 객체 생성하고 파이어베이스 서버로 전송
     */
    fun onClickOrderBtn() {
        getPrice()
        val newOrderRequest = createOrderRequest()
        repository.writeOrderRequest(newOrderRequest)
        orderRequest.value = newOrderRequest
    }


    private fun createOrderRequest(): OrderRequest = OrderRequest(
            customerUid = tmpUid,
            stores = storeList as ArrayList<Store>,
            deliveryCharge = price.get(),
            destination = destination.get()!!,
            message = message.get()
    )


    /**
     * 각 주문 장소를 거치고 목적지까지 도착하는 길의 거리를 계산하고 리턴하는 함수
     */
    private suspend fun getDistance(): Int? {
        var start: String? = null
        var waypoints: String? = null
        var goal: String?

        // storeList 및 destination 에서 place 추출
        val placesOfStore: List<Place> = storeList.map { store -> store.place }
        val placeOfDestination: Place = destination.get()!!

        // placesOfStore의 첫 번째 장소를 start로, 그 외의 장소를 waypoints로 설정
        for (i in placesOfStore.indices) {
            val place = placesOfStore[i]
            when (i) {
                0 -> start = "${place.longitude},${place.latitude}"
                1 -> waypoints = "${place.longitude},${place.latitude}"
                else -> waypoints += ":${place.longitude},${place.latitude}"
            }
        }

        // destination을 goal로 설정
        goal = "${placeOfDestination.longitude},${placeOfDestination.latitude}"

        Log.i(TAG, "start = $start")
        Log.i(TAG, "goal = $goal")
        Log.i(TAG, "waypoints = $waypoints")
        return repository.getDistance(start = start!!, goal = goal, waypoints = waypoints)
    }

    /**
     * 배달료 계산해서 price에 set하는 함수
     */
    private fun getPrice() {
        viewModelScope.launch {
            try {
                val distance = getDistance()
                if (distance != null) {
                    price.set(storeList.size * 1000 + (distance / 100f).roundToInt() * 100)
                }
            } catch (e: Exception) {
                Log.i(TAG, e.printStackTrace().toString())
            }
        }
    }
}