package com.santaistiger.gomourcustomerapp.ui.viewmodel

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
import com.santaistiger.gomourcustomerapp.utils.NotEnteredException
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class DoOrderViewModel : ViewModel() {
    companion object {
        private const val TAG = "DoOrderViewModel"
    }

    val storeList = ObservableArrayList<Store>()
    val destination = ObservableField<Place>()
    val message = ObservableField<String>()
    val price = ObservableInt()
    val orderRequest = MutableLiveData<OrderRequest?>()
    val exception = MutableLiveData<Exception?>()

    private val repository: Repository = RepositoryImpl

    init {
        init()
    }

    private fun init() {
        appendStore()
        destination.set(Place())
    }

    fun appendStore() {
        storeList.add(Store())
    }

    fun doneNavigateWaitMatch() {
        orderRequest.value = null
        storeList.clear()
        message.set("")
        price.set(0)
        init()
    }

    /**
     * 주문하기 객체 생성하고 파이어베이스 서버로 전송
     */
    fun onClickOrderBtn() {
        viewModelScope.launch {
            try {
                checkInput()
                getDeliveryCharge().join()
                createOrderRequest().let {
                    repository.writeOrderRequest(it)
                    orderRequest.value = it
                }
            } catch (e: NotEnteredException) {
                exception.value = e
            }
        }

    }

    private fun checkInput() {
        for (store in storeList) {
            if (store.place.roadAddressName.isNullOrEmpty()) {
                throw NotEnteredException("모든 가게의 주소를 입력해주세요.")
            } else if (store.menu.isNullOrEmpty()) {
                throw NotEnteredException("모든 가게의 메뉴를 입력해주세요.")
            }
        }
        if (destination.get()!!.roadAddressName.isNullOrEmpty()) {
            throw NotEnteredException("배달받을 주소를 입력해주세요.")
        }
    }

    private fun createOrderRequest(): OrderRequest = OrderRequest(
        customerUid = repository.getUid(),
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
        val goal: String = destination.get()!!.let { place ->
            "${place.longitude},${place.latitude}"
        }

        // storeList 및 destination 에서 place 추출
        storeList.forEachIndexed { i, store ->
            when (i) {
                0 -> start = "${store.place.longitude},${store.place.latitude}"
                1 -> waypoints = "${store.place.longitude},${store.place.latitude}"
                else -> waypoints += "|${store.place.longitude},${store.place.latitude}"
            }
        }
        Log.i(TAG, "start = $start \ngoal = $goal \nwaypoints = $waypoints")
        return repository.getDistance(start = start!!, goal = goal, waypoints = waypoints)
    }

    /**
     * 배달료 계산해서 price에 set하는 함수
     */
    fun getDeliveryCharge() = viewModelScope.launch {
        try {
            checkInput()
            val distance = getDistance()
            if (distance != null) {
                price.set(storeList.size * 1000 + (distance / 100f).roundToInt() * 100)
            }
        } catch (e: NotEnteredException) {
            exception.value = e
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun doneExceptionProcess() {
        exception.value = null
    }
}