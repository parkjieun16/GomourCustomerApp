/**
 * created by Kang Gumsil
 */
package com.santaistiger.gomourcustomerapp.ui.viewmodel

import android.util.Log
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.databinding.ObservableParcelable
import androidx.lifecycle.LiveData
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
    
    private val _orderRequest = MutableLiveData<OrderRequest?>()
    val orderRequest: LiveData<OrderRequest?> = _orderRequest

    private val _exception = MutableLiveData<Exception?>()
    val exception: LiveData<Exception?> = _exception

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
        _orderRequest.value = null
        storeList.clear()
        message.set("")
        price.set(0)
        init()
    }

    /** 주문하기 버튼 클릭 시 Order 객체 생성하고 데이터베이스 서버의 order_request 테이블에 write */
    fun createOrder() = viewModelScope.launch {
        try {
            checkInput()
            getDeliveryCharge().join()
            createOrderRequest().let {
                repository.writeOrderRequest(it)
                _orderRequest.value = it
            }
        } catch (e: NotEnteredException) {
            _exception.value = e
        }
    }


    private fun checkInput()  {
        for (store in storeList) {
            if (store.place.roadAddressName.isNullOrEmpty()) {
                throw NotEnteredException("모든 칸에 주문 장소를 설정해주세요")
            } else if (store.menu.isNullOrEmpty()) {
                throw NotEnteredException("모든 칸에 요청사항을 입력해주세요")
            }
        }
        if (destination.get()!!.roadAddressName.isNullOrEmpty()) {
            throw NotEnteredException("배달 장소를 설정해주세요")
        }
    }

    private fun createOrderRequest(): OrderRequest = OrderRequest(
        customerUid = repository.getUid(),
        stores = storeList as ArrayList<Store>,
        deliveryCharge = price.get(),
        destination = destination.get()!!,
        message = message.get()
    )

    /** 각 주문 장소를 거치고 배달 장소까지 도착하는 길의 거리를 계산하고 리턴하는 함수 */
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

    /** 배달료 계산해서 price에 설정하는 함수 */
    fun getDeliveryCharge() = viewModelScope.launch {
        try {
            checkInput()
            val distance = getDistance()
            if (distance != null) {
                price.set(storeList.size * 1000 + (distance / 100f).roundToInt() * 100)
            }
        } catch (e: NotEnteredException) {
            _exception.value = e
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun doneExceptionProcess() {
        _exception.value = null
    }
}