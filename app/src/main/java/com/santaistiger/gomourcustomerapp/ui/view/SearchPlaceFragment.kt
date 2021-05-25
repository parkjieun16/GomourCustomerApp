/**
 * created by Kang Gumsil
 */
package com.santaistiger.gomourcustomerapp.ui.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.santaistiger.gomourcustomerapp.R
import com.santaistiger.gomourcustomerapp.data.model.Place
import com.santaistiger.gomourcustomerapp.databinding.FragmentSearchPlaceBinding
import com.santaistiger.gomourcustomerapp.ui.base.BaseActivity
import com.santaistiger.gomourcustomerapp.ui.customview.RoundedAlertDialog
import com.santaistiger.gomourcustomerapp.ui.viewmodel.DoOrderViewModel
import com.santaistiger.gomourcustomerapp.ui.viewmodel.SearchPlaceViewModel
import kotlinx.android.synthetic.main.activity_base.*
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

class SearchPlaceFragment : Fragment(), MapView.POIItemEventListener {
    companion object {
        private val DANKOOKUNIV_LOCATION =
            MapPoint.mapPointWithGeoCoord(37.323177, 127.125758)
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

        init(inflater, container)
        addObserver()

        return binding.root
    }

    /** viewModel, binding, 툴바 및 지도 설정 */
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

        // 툴바 설정
        (requireActivity() as BaseActivity).setToolbar(
            requireContext(), false, null, false
        )

        // 지도 설정
        initKakaoMap()
    }

    private fun addObserver() {
        addPlacesObserver()
        addSearchBtnObserver()
    }

    /** 카카오 지도 MapView를 생성한 후, POIITem 이벤트 리스너를 설정하고 지도의 중심점을 단국대학교로 이동한다 */
    private fun initKakaoMap() {
        mapView = MapView(context).apply {
            binding.mapView.addView(this)
            setPOIItemEventListener(this@SearchPlaceFragment)
            setMapCenterPointAndZoomLevel(DANKOOKUNIV_LOCATION, 2, true)
        }
    }

    /**
     * SearchPlaceViewModel의 places 값이 변경되면,
     * 기존의 pin을 모두 제거한 후 각 place의 위치에 pin을 찍도록 설정한다.
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
     * 현재 보고있는 지도의 중심을 기준으로 장소를 검색하기 위해,
     * 검색 버튼 터치 시, SearchPlaceViewModel의 curMapPos에 현재 위치를 저장하도록 설정한다.
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
     * pin 터치 시 나타나는 말풍선을 터치했을 때, 해당 장소를 선택할 것이냐는 알림창을 띄운 후,
     * 사용자가 '확인'을 터치하면 주문하기화면(DoOrderFragment)으로 넘어가면서
     * 선택한 장소의 위치(위도/경도)와 도로명주소를 argument로 전달한다.
     */
    override fun onCalloutBalloonOfPOIItemTouched(
        mapView: MapView?,
        item: MapPOIItem?,
        ballonBtnType: MapPOIItem.CalloutBalloonButtonType?
    ) {

        if (item != null) {
            RoundedAlertDialog()
                .setMessage(
                    String.format(
                        resources.getString(R.string.check_select_place),
                        item.itemName
                    )
                )
                .setPositiveButton(resources.getString(R.string.ok)) {
                    val position = SearchPlaceFragmentArgs.fromBundle(requireArguments()).position
                    val place = item.userObject as Place
                    Log.i(TAG, "item: $item")

                    when (position) {
                        -1 -> sharedViewModel.destination.set(place) // 배달 장소 주소 변경
                        else -> sharedViewModel.storeList[position].place = place // 가게 주소 변경
                    }

                    findNavController().navigate(
                        SearchPlaceFragmentDirections.actionSearchPlaceFragmentToDoOrderFragment()
                    )
                }
                .setNegativeButton(resources.getString(R.string.cancel), null)
                .show(requireActivity().supportFragmentManager, "search place fragment")
        }
    }

    /** 인터페이스 구현을 위해 override한 메소드들 */
    override fun onPOIItemSelected(mapView: MapView?, item: MapPOIItem?) {}
    override fun onDraggablePOIItemMoved(mapView: MapView?, item: MapPOIItem?, point: MapPoint?) {}
    override fun onCalloutBalloonOfPOIItemTouched(mapView: MapView?, item: MapPOIItem?) {}
}
