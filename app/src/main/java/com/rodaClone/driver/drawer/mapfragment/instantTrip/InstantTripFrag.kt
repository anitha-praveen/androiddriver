package com.rodaClone.driver.drawer.mapfragment.instantTrip

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.rodaClone.driver.BR
import com.rodaClone.driver.MyApplication
import com.rodaClone.driver.R
import com.rodaClone.driver.base.BaseFragment
import com.rodaClone.driver.connection.BaseResponse
import com.rodaClone.driver.databinding.FragInstantTripBinding
import com.rodaClone.driver.drawer.DrawerActivity
import com.rodaClone.driver.ut.Config
import com.rodaClone.driver.ut.SessionMaintainence
import kotlinx.coroutines.launch
import javax.inject.Inject

class InstantTripFrag : BaseFragment<FragInstantTripBinding, InstantTripVm>(),
    InstantTripNavigator, OnMapReadyCallback {

    lateinit var binding: FragInstantTripBinding
    private lateinit var googleMap: GoogleMap
   // private val args: InstantTripFragArgs by navArgs()
    private var isCameraMoved = false



    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    val vm by lazy {
        ViewModelProvider(this, viewModelFactory).get(InstantTripVm::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                closeFragment()
            }
        })

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getmBinding()
        vm.setNavigator(this)
        binding.backImg.setOnClickListener { closeFragment() }
        getArgs()
        setupMap()

    }

    private fun getArgs() {
        arguments?.let {
            vm.isDropPressed.set(it.getBoolean(Config.dropaddresspressed))
            if (vm.isDropPressed.get()) {
                vm.showMarker.set(true)
                vm.dropAddress.set(it.getString(Config.drop_address))
                vm.dropLatLng.set(LatLng(it.getDouble(Config.updatedLat),it.getDouble(Config.updatedLng)))
                vm.pickupAddress.set(it.getString(Config.pickupAddress))
                vm.vmPickupLatLng.set(LatLng(it.getDouble(Config.pick_lat),it.getDouble(Config.pick_lng)))
            }
        }

    }

    private fun setupMap() {
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.instant_trip_map) as? SupportMapFragment
        mapFragment!!.getMapAsync(this)
        vm.fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
    }


    override fun getLayoutId() = R.layout.frag_instant_trip

    override fun getBR() = BR.viewModel

    override fun getVMClass() = vm

    fun closeFragment() {
        findNavController().navigate(R.id.map_fragment)
    }

    override fun onMapReady(p0: GoogleMap) {
        this.googleMap = p0
        vm.googleMap = p0
        if (!vm.isDropPressed.get()) {
            setInitialLocation()
            vm. moveCamera(
                googleMap,
                LatLng(vm.vmPickupLatLng.get()!!.latitude, vm.vmPickupLatLng.get()!!.longitude),
                vm.zoom
            )
        } else {
            val toDropLat = LatLng(vm.dropLatLng.get()!!.latitude, vm.dropLatLng.get()!!.longitude)
            vm.moveCamera(googleMap, toDropLat, vm.zoom)
        }
        setCameraListener()

    }

    private fun setInitialLocation() {
        if (!vm.session.getString(SessionMaintainence.CURRENT_LATITUDE).isNullOrEmpty() &&
            !vm.session.getString(SessionMaintainence.CURRENT_LONGITUDE).isNullOrEmpty()
        ) {
            val latLng = LatLng(
                vm.session.getString(SessionMaintainence.CURRENT_LATITUDE)!!.toDouble(),
                vm.session.getString(SessionMaintainence.CURRENT_LONGITUDE)!!.toDouble()
            )
            vm.vmPickupLatLng.set(latLng)
            vm.getAddressFromLatLng()

        } else
            vm.viewModelScope.launch { vm.getLastLocation(requireContext(), googleMap) }
    }

    private fun setCameraListener() {
        googleMap.setOnCameraMoveStartedListener {

            when (it) {
                GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE -> {
                    isCameraMoved = true
                    if (!vm.isDropPressed.get()) {
                       // vm.showPickupText.set(true)
                       // vm.showDropText.set(false)
                    } else {
                        vm.showPickupText.set(false)
                        vm.showDropText.set(true)
                    }
                }
                GoogleMap.OnCameraMoveStartedListener.REASON_API_ANIMATION -> {


                }
                GoogleMap.OnCameraMoveStartedListener.REASON_DEVELOPER_ANIMATION -> {


                }
            }

        }

        googleMap.setOnCameraIdleListener {
            if (isCameraMoved){
                if (!vm.isDropPressed.get()) {
                    // vm.vmPickupLatLng.set(googleMap.cameraPosition.target)
                    //  vm.showDropText.set(true)
                } else {
                    vm.dropLatLng.set(googleMap.cameraPosition.target)
                    vm.getAddressFromLatLng()
                    vm.showPickupText.set(true)
                }
            }
        }
    }

    override fun getCtx(): Context {
        return if(isAdded) requireContext() else MyApplication.appContext!!
    }

    override fun openSearchPlace() {
        val bundle= bundleOf(
            Config.pickupAddress to  vm.pickupAddress.get()!!,
            Config.pick_lat to  vm.vmPickupLatLng.get()!!.latitude,
            Config.pick_lng to  vm.vmPickupLatLng.get()!!.longitude)
        findNavController().navigate(R.id.instant_to_searchPlace, bundle)
    }

    override fun pickAddressPressed() {
        vm.isDropPressed.set(false)
        vm.isPicAdresspressed.set(true)
        vm.moveCamera(googleMap, vm.vmPickupLatLng.get(), vm.zoom)
    }

    override fun currentLocation() {
        setInitialLocation()
        vm.moveCamera(googleMap, vm.vmPickupLatLng.get(), vm.zoom)

    }

    override fun showDialog() {
        val dialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        dialog.setContentView(R.layout.get_user_phone_number_dialog)
        dialog.show()
        val number = dialog.findViewById<EditText>(R.id.user_phone_number)
        number?.hint = vm.translationModel?.txt_enter_pass_num?:""

        dialog.findViewById<TextView>(R.id.enter_pass_no_txt)?.text = vm.translationModel?.txt_enter_pass_num?:""

        val buttonSubmit = dialog.findViewById<MaterialButton>(R.id.instant_confirm)
        buttonSubmit?.text = vm.translationModel?.text_start_trip?:""
        val closeButton = dialog.findViewById<ImageView>(R.id.close)
        closeButton?.setOnClickListener {
            dialog.dismiss()
        }
        buttonSubmit?.setOnClickListener {
            val numberTxt = number?.text.toString()
            when {
                numberTxt.isNullOrEmpty() -> {
                    showMessage(vm.translationModel?.txt_phone_cannot_be_empty.toString())
                }
                numberTxt.length<10 -> {
                    showMessage(vm.translationModel?.txt_phone_error.toString())
                }
                else -> {
                    vm.userPhoneNumber.set(numberTxt)
                    vm.createRequest()
                    dialog.dismiss()
                    vm.showButton.set(true)
                }
            }
        }
        dialog.setCancelable(false)
        dialog.show()
    }

    override fun openTrip(data: BaseResponse.DataObjectsAllApi) {
        (activity as DrawerActivity).openTripData(data.requestData, data.requestData?.driver!!)
    }


}