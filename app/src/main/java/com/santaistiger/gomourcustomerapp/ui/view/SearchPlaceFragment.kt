package com.santaistiger.gomourcustomerapp.ui.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.santaistiger.gomourcustomerapp.R
import com.santaistiger.gomourcustomerapp.data.model.Place
import com.santaistiger.gomourcustomerapp.databinding.FragmentSearchPlaceBinding
import com.santaistiger.gomourcustomerapp.ui.viewmodel.DoOrderViewModel
import com.santaistiger.gomourcustomerapp.ui.viewmodel.SearchPlaceViewModel
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

class SearchPlaceFragment : Fragment(), MapView.POIItemEventListener {
    companion object {
        private val DANKOOKUNIV_LOCATION =
            MapPoint.mapPointWithGeoCoord(37.32224683322665, 127.12683613068711)
        private const val TAG = "SearchPlaceFragment"
    }

    private lateinit var binding: FragmentSearchPlaceBinding
    private lateinit var viewModel: SearchPlaceViewModel
    private lateinit var mapView: MapView
    private val sharedViewModel: DoOrderViewModel by activityViewModels()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        setToolbar()
        init(inflater, container)
        initKakaoMap()
        addPlacesObserver()
        addSearchBtnObserver()

        return binding.root
    }

    private fun setToolbar() {
        requireActivity().apply {
            toolbar.visibility = View.GONE    // 툴바 숨기기
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED) // 스와이프 비활성화
        }
    }

    /**
     * viewModel 및 binding 설정
     */
    private fun init(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_search_place,
                container,
                false
        )
        viewModel = ViewModelProvider(this).get(SearchPlaceViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    /**
     * 카카오 지도 MapView를 띄우고, POIITem 이벤트 리스너를 설정하고,
     * 지도의 중심점을 단국대학교로 이동
     */
    private fun initKakaoMap() {
        CoroutineScope(Dispatchers.Main).launch {
            mapView = MapView(context).apply {
                binding.mapView.addView(this)
                setPOIItemEventListener(this@SearchPlaceFragment)
                setMapCenterPointAndZoomLevel(DANKOOKUNIV_LOCATION, 2, true)
            }
        }
    }

    /**
     * 검색 버튼이 클릭되면, 현재 지도의 위치를 전달해주는 Observer 추가
     */
    private fun addSearchBtnObserver() {
        viewModel.buttonClicked.observe(viewLifecycleOwner, { clicked ->
            if (clicked) {
                viewModel.curMapPos = mapView.mapCenterPoint.mapPointGeoCoord
                viewModel.doneSearchBtnClick()
            }
        })
    }

    /**
     * viewModel의 places를 관찰하는 observer 추가
     * places에 요소가 있으면 기존의 마커를 모두 지우고 places 요소의 위치에 마커를 표시한다.
     */
    private fun addPlacesObserver() {
        viewModel.places.observe(viewLifecycleOwner, { places ->
            mapView.removeAllPOIItems()
            for (place: Place in places) {
                Log.i(TAG, "$place")

                MapPOIItem().apply {
                    itemName = place.placeName
                    mapPoint = MapPoint.mapPointWithGeoCoord(place.latitude!!, place.longitude!!)
                    markerType = MapPOIItem.MarkerType.BluePin
                    selectedMarkerType = MapPOIItem.MarkerType.RedPin
                    userObject = place
                    mapView.addPOIItem(this)
                }
            }
        })
    }

    /**
     * 마커 클릭하면 위에 표시되는 말풍선 클릭했을 때, 아래와 같은 알림창 띄우기
     * 메시지: {장소명}을 선택하시겠습니까?
     * 버튼: 확인 / 취소
     * 확인 누르면 '주문하기'화면으로 넘어가면서 선택한 장소의 위치(위도/경도), 도로명주소 넘기기
     */
    override fun onCalloutBalloonOfPOIItemTouched(
            mapView: MapView?,
            item: MapPOIItem?,
            ballonBtnType: MapPOIItem.CalloutBalloonButtonType?) {

        if (item != null) {
            AlertDialog.Builder(requireActivity())
                    .setMessage("${item.itemName}을 선택하시겠습니까?")
                    .setPositiveButton("확인") { _, _ ->
                        val position = SearchPlaceFragmentArgs.fromBundle(requireArguments()).position
                        val place = item.userObject as Place
                        Log.i(TAG, "item: $item")

                        if (position == -1) { // 목적지 주소 변경
                            sharedViewModel.destination.set(place)
                        } else { // 가게 주소 변경
                            sharedViewModel.storeList[position].place = place
                        }
                        findNavController().navigate(
                            SearchPlaceFragmentDirections.actionSearchPlaceFragmentToDoOrderFragment()
                        )
                    }
                    .setNegativeButton("취소", null)
                    .create()
                    .show()
        }
    }

    /**
     * 인터페이스 구현을 위해 override한 메소드들
     * 따로 구현하지 않음
     */
    override fun onPOIItemSelected(mapView: MapView?, item: MapPOIItem?) {  }
    override fun onDraggablePOIItemMoved(mapView: MapView?, item: MapPOIItem?, point: MapPoint?) {  }
    override fun onCalloutBalloonOfPOIItemTouched(mapView: MapView?, item: MapPOIItem?) {    }
}
