package com.rodaClone.driver.drawer.mapfragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.rodaClone.driver.BR
import com.rodaClone.driver.MyApplication
import com.rodaClone.driver.R
import com.rodaClone.driver.base.BaseFragment
import com.rodaClone.driver.databinding.FragmentMapBinding
import com.rodaClone.driver.dialogs.cancel.CancelDialog.Companion.TAG
import com.rodaClone.driver.drawer.DrawerActivity
import com.rodaClone.driver.ut.Config
import com.rodaClone.driver.ut.MyFirebaseMessagingService
import com.rodaClone.driver.ut.SessionMaintainence
import com.rodaClone.driver.ut.Utilz
import javax.inject.Inject


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class MapFragment : BaseFragment<FragmentMapBinding, MapFragmentVM>(),
    MapFragmentNavigator, OnMapReadyCallback {

    companion object {
        var Tag = "mapsss"
    }

    var backpressed = false
    lateinit var _binding: FragmentMapBinding
    private lateinit var googleMap: GoogleMap

    @Inject
    lateinit var viewModelFagtory: ViewModelProvider.Factory
    val firstVM by lazy {
        ViewModelProvider(this, viewModelFagtory).get(MapFragmentVM::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (backpressed)
                    requireActivity().finishAffinity()
                backpressed = true
                showMessage(firstVM.translationModel?.txt_press_back_again ?: "")
                Handler(Looper.getMainLooper()).postDelayed(
                    { backpressed = false }, 2000
                )
            }
        })


    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            subscriptionDetails,
            IntentFilter(Config.RECEIVE_SUBSCRIPTION_DETAILS)
        )
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            currentLocClick,
            IntentFilter(Config.RECEIVE_CURR_LOC_CLICK)
        )
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(subscriptionDetails)
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(currentLocClick)
    }



    private val subscriptionDetails: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            firstVM.showRemainingDays.set(intent.getBooleanExtra(Config.SHOW_SUBSCRIBE, false))
            if (firstVM.showRemainingDays.get())
                firstVM.remainingDays.set(
                    firstVM.translationModel?.txt_remaining_subs + " " + intent.getStringExtra(
                        Config.BALANCE_SUBSCRIPTION_DAYS
                    )
                )
        }
    }

    private val currentLocClick = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            firstVM.getLastLocation(requireContext(), googleMap)
        }
    }

//    private fun getJsonDataFromAsset(): JSONArray? {
//        return try {
//            val jsonString = requireContext().assets.open("latLong.json").bufferedReader().use { it.readText() }
//            JSONArray(jsonString)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            null
//        }
//    }

    private fun setupMap() {
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_fragment) as? SupportMapFragment
        mapFragment!!.getMapAsync(this)
        firstVM.fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = getmBinding()
        firstVM.setNavigator(this)
        setupMap()

        _binding.switchCompat.setOnCheckedChangeListener { _, _ ->
            firstVM.toggleStatus()
        }
    }

    override fun getLayoutId() = R.layout.fragment_map

    override fun getBR() = BR.viewModel

    override fun getVMClass() = firstVM

    override fun onMapReady(p0: GoogleMap) {
        this.googleMap = p0
        firstVM.googleMap = p0
//        val colors = intArrayOf(Color.YELLOW, Color.parseColor("#FF0000"),Color.GREEN,Color.BLACK,Color.WHITE)
//        val startPoints = floatArrayOf(0.2f, 0.4f,0.6f,0.8f,1f)
//        val gradient = Gradient(colors, startPoints)
//        val data = generateHeatMapData()
//        val heatMapProvider = HeatmapTileProvider.Builder()
//            .weightedData(data) // load our weighted data
//            .radius(50)
//            .gradient(gradient)// optional, in pixels, can be anything between 20 and 50
//            .maxIntensity(1000.0)
//            .build()
//        googleMap.addTileOverlay(TileOverlayOptions().tileProvider(heatMapProvider))
        setInitialLocation()
        //firstVM.getLastLocation(requireContext(), googleMap)

    }

    private fun setInitialLocation() {
        if (!firstVM.session.getString(SessionMaintainence.CURRENT_LATITUDE).isNullOrEmpty() &&
            !firstVM.session.getString(SessionMaintainence.CURRENT_LONGITUDE).isNullOrEmpty()
        ) {
            val latLng = LatLng(
                firstVM.session.getString(SessionMaintainence.CURRENT_LATITUDE)!!.toDouble(),
                firstVM.session.getString(SessionMaintainence.CURRENT_LONGITUDE)!!.toDouble()
            )
            firstVM.markerAnimation(googleMap, latLng, 16.0f)
            firstVM.currLatLng!!.value = latLng
            //firstVM.getAddressFromLatLng()
        }
        getAct()?.let { firstVM.requestLocationUpdates(it) }
    }

    override fun openInstantTrip() {
        if (findNavController().currentDestination?.label == "Home")
            findNavController().navigate(R.id.map_to_instantTrip)
    }

    override fun getAct(): FragmentActivity? {
        return if (isAdded) requireActivity()
        else null
    }

    override fun startOnetimeReq(location: Location) {
        activity?.let { act ->
            (act as DrawerActivity).vm.isBlocked?.let {
                if (!it)
                    act.workManager()
            }
        }
    }

    override fun getCtx(): Context? {
        return if (isAdded) requireContext()
        else MyApplication.appContext
    }

    override fun openSos() {
        findNavController().navigate(R.id.map_to_sos)
    }

    override fun getMarkerIcon(icPickPin: Int): BitmapDescriptor? {
        return if (getCtx() == null)
            null
        else Utilz.getBitmapFromDrawable(
            getCtx()!!,
            icPickPin
        )?.let {
            BitmapDescriptorFactory.fromBitmap(
                it
            )
        }

    }

//    private fun generateHeatMapData(): ArrayList<WeightedLatLng> {
//        val data = ArrayList<WeightedLatLng>()
//        // call our function which gets json data from our asset file
//        val jsonData = getJsonDataFromAsset()
//        // ensure null safety with let call
//        jsonData?.let {
//            // loop over each json object
//            for (i in 0 until it.length()) {
//                // parse each json object
//                val entry = it.getJSONObject(i)
//                val lat = entry.getDouble("lat")
//                val lon = entry.getDouble("lon")
//                val density = entry.getDouble("intensity")
//                // optional: remove edge cases like 0 population density values
//                if (density != 0.0) {
//                    val weightedLatLng = WeightedLatLng(LatLng(lat, lon), density)
//                    data.add(weightedLatLng)
//                }
//            }
//        }
//        return data
//    }


}