package com.santaistiger.gomourcustomerapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.santaistiger.gomourcustomerapp.data.model.Place
import com.santaistiger.gomourcustomerapp.data.network.map.*
import com.santaistiger.gomourcustomerapp.data.repository.Repository
import com.santaistiger.gomourcustomerapp.data.repository.RepositoryImpl
import kotlinx.coroutines.launch
import net.daum.mf.map.api.MapPoint


class SearchPlaceViewModel: ViewModel() {
    companion object {
        private const val TAG = "SearchPlaceViewModel"
    }
    private val repository: Repository = RepositoryImpl

    val places = MutableLiveData<List<Place>>()
    val buttonClicked = MutableLiveData<Boolean>()
    var placeName = String()
    lateinit var curMapPos: MapPoint.GeoCoordinate

    init {
        buttonClicked.value = false
    }


    /**
     * 장소 검색 버튼 클릭하면 로컬 키워드 검색 api로 5개(일단) 장소 찾고, 마커로 표시하기
     * 그 중 하나의 마커를 선택하면, 해당 주소의 위치(위도, 경도) 로그 띄우기
     * 네트워크 연결하는 부분이므로, 나중에 코루틴 사용하도록 변경하기
     */
    fun onSearchBtnClick() {
        Log.i("MapViewModel", "search button clicked!, placeName: $placeName")
        buttonClicked.value = true

        viewModelScope.launch {
            try {
                places.value = repository.searchPlace(placeName, curMapPos)
            } catch (e: Exception) {
                e.printStackTrace()
                places.value = ArrayList()
            }
        }
    }

    fun doneSearchBtnClick() {
        buttonClicked.value = false
    }
}