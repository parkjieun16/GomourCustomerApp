/**
 * created by Kang Gumsil
 */
package com.santaistiger.gomourcustomerapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.santaistiger.gomourcustomerapp.data.model.Place
import com.santaistiger.gomourcustomerapp.data.repository.Repository
import com.santaistiger.gomourcustomerapp.data.repository.RepositoryImpl
import kotlinx.coroutines.launch
import net.daum.mf.map.api.MapPoint

class SearchPlaceViewModel : ViewModel() {
    companion object {
        private const val TAG = "SearchPlaceViewModel"
    }

    private val repository: Repository = RepositoryImpl

    val places = MutableLiveData<List<Place>>()
    val buttonClicked = MutableLiveData<Boolean>()
    var placeName = String()
    lateinit var curMapPos: MapPoint.GeoCoordinate

    /** 검색 버튼 클릭 시, 현재 보고있는 지도의 중심을 기준으로, 15개의 장소 찾고 pin(POI item) 찍기 */
    fun onSearchBtnClick() {
        Log.i("MapViewModel", "search button clicked!, placeName: $placeName")
        buttonClicked.value = true

        viewModelScope.launch {
            try {
                places.value = repository.searchPlace(placeName, curMapPos)
            } catch (e: Exception) {
                places.value = ArrayList()
                e.printStackTrace()
            }
        }
    }

    fun doneSearchBtnClick() {
        buttonClicked.value = false
    }
}