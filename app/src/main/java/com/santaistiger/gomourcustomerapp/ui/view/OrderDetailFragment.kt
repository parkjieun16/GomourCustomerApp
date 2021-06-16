/**
 * created by Kang Gumsil
 */
package com.santaistiger.gomourcustomerapp.ui.view

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.santaistiger.gomourcustomerapp.R
import com.santaistiger.gomourcustomerapp.data.model.Place
import com.santaistiger.gomourcustomerapp.data.repository.Repository
import com.santaistiger.gomourcustomerapp.data.repository.RepositoryImpl
import com.santaistiger.gomourcustomerapp.databinding.FragmentOrderDetailBinding
import com.santaistiger.gomourcustomerapp.ui.base.BaseActivity
import com.santaistiger.gomourcustomerapp.ui.customview.RoundedAlertDialog
import com.santaistiger.gomourcustomerapp.ui.viewmodel.OrderDetailViewModel
import com.santaistiger.gomourcustomerapp.ui.viewmodel.OrderDetailViewModelFactory
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.coroutines.*
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView


class OrderDetailFragment : Fragment() {
    companion object {
        private val DANKOOKUNIV_LOCATION =
            MapPoint.mapPointWithGeoCoord(37.323177, 127.125758)
        private const val TAG = "OrderDetailFragment"
    }

    private lateinit var binding: FragmentOrderDetailBinding
    private lateinit var viewModel: OrderDetailViewModel
    private lateinit var mapView: MapView

    private val repository: Repository = RepositoryImpl

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        init(inflater, container)
        setObserver()
        addClickListener()

        return binding.root
    }

    /** viewModel, binding, 툴바 및 지도 설정 */
    private fun init(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_order_detail,
            container,
            false
        )


        val orderId = OrderDetailFragmentArgs.fromBundle(requireArguments()).orderId

        viewModel = ViewModelProvider(this, OrderDetailViewModelFactory(orderId))
            .get(OrderDetailViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // 툴바 설정
        (requireActivity() as BaseActivity).setToolbar(
            requireContext(), true, resources.getString(R.string.lock_up_order), true)

        // 지도 설정
        initKakaoMap()
    }

    private fun setObserver() {
        setOrderObserver()
        setCallBtnObserver()
        setTextBtnObserver()
    }

    private fun addClickListener() {
        // 비용 및 계좌번호창 옆의 복사 버튼 터치시 비용 및 계좌번호가 복사되도록 설정
        binding.cvPrice.binding.btnCopyAccount.setOnClickListener { copyAccountInfo() }
    }

    private fun copyAccountInfo() {
        val context = requireContext()
        val clipboard =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        val clip: ClipData = ClipData.newPlainText(
            "delivery man account information",
            "${binding.cvPrice.binding.tvAccount.text}\n${binding.cvPrice.binding.tvPrice.text}"
        )
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, resources.getString(R.string.message_copy_account), Toast.LENGTH_SHORT).show()
    }

    /** 카카오 지도 MapView를 생성한 후, POIITem 이벤트 리스너를 설정하고 지도의 중심점을 단국대학교로 이동 */
    private fun initKakaoMap() {
        mapView = MapView(context).apply {
            binding.mapView.addView(this)
            setMapCenterPointAndZoomLevel(DANKOOKUNIV_LOCATION, 2, true)
        }
    }

    /** 주문 장소와 배달 장소에 pin(POIItem)을 찍는 함수. */
    private fun setOrderObserver() {
        viewModel.order.observe(viewLifecycleOwner, Observer { order ->
            viewModel.getAccountInfo()

            // POI item이 없으면 POI item 생성
            if (mapView.poiItems.isEmpty()) {
                for (store in order?.stores!!) {
                    setPOIItem(
                        store.place,
                        MapPOIItem.MarkerType.BluePin,
                        MapPOIItem.MarkerType.RedPin
                    )
                }
                order.destination?.let {
                    setPOIItem(
                        it,
                        MapPOIItem.MarkerType.RedPin,
                        MapPOIItem.MarkerType.BluePin
                    )
                }
            }
        })
    }

    /** 사용자가 '배달 기사에게 문자하기' 버튼을 터치하면 문자앱으로 이동 */
    private fun setTextBtnObserver() {
        viewModel.isTextBtnClick.observe(viewLifecycleOwner, Observer { clicked ->
            if (clicked) {
                RoundedAlertDialog()
                    .setMessage(resources.getString(R.string.check_sms))
                    .setPositiveButton(resources.getString(R.string.ok)) {
                        CoroutineScope(Dispatchers.IO).launch {
                            val deliveryManUid = viewModel.getDeliveryManUid()
                            val deferredPhone =
                                async { repository.readDeliveryManPhone(deliveryManUid) }
                            startActivity(
                                Intent(Intent.ACTION_SENDTO)
                                    .setData(Uri.parse("smsto:${deferredPhone.await()}"))
                                    .putExtra("sms_body", resources.getString(R.string.message_sms_greeting))
                            )
                        }
                        viewModel.doneTextBtnClick()
                    }
                    .setNegativeButton(resources.getString(R.string.cancel)) { viewModel.doneTextBtnClick() }
                    .show(requireActivity().supportFragmentManager, "rounded alert dialog")
            }
        })
    }


    /** 사용자가 '배달 기사에게 전화하기' 버튼을 터치하면 전화앱으로 이동 */
    private fun setCallBtnObserver() {
        viewModel.isCallBtnClick.observe(viewLifecycleOwner, Observer { clicked ->
            if (clicked) {
                RoundedAlertDialog()
                    .setMessage(resources.getString(R.string.check_call))
                    .setPositiveButton(resources.getString(R.string.ok)) {
                        CoroutineScope(Dispatchers.IO).launch {
                            val deliveryManUid = viewModel.getDeliveryManUid()
                            val deferredPhone =
                                async { repository.readDeliveryManPhone(deliveryManUid) }
                            startActivity(
                                Intent(Intent.ACTION_DIAL).setData(Uri.parse("tel:${deferredPhone.await()}"))
                            )
                        }
                        viewModel.doneCallBtnClick()
                    }
                    .setNegativeButton(resources.getString(R.string.cancel)) { viewModel.doneCallBtnClick() }
                    .show(requireActivity().supportFragmentManager, "rounded alert dialog")
            }
        })
    }

    /** 지도에 pin(POIItem)을 찍는 함수 */
    private fun setPOIItem(
        place: Place,
        marker: MapPOIItem.MarkerType,
        selectedMarker: MapPOIItem.MarkerType
    ) {
        MapPOIItem().apply {
            itemName = place.placeName
            mapPoint = MapPoint.mapPointWithGeoCoord(
                place.latitude!!,
                place.longitude!!
            )
            markerType = marker
            selectedMarkerType = selectedMarker
            userObject = place
            mapView.addPOIItem(this)
        }
    }
}