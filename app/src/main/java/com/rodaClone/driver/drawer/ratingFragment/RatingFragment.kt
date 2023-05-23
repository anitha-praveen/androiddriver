package com.rodaClone.driver.drawer.ratingFragment

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.rodaClone.driver.BR
import com.rodaClone.driver.R
import com.rodaClone.driver.base.BaseFragment
import com.rodaClone.driver.connection.responseModels.RequestResponseData
import com.rodaClone.driver.databinding.FragmentRatingBinding
import com.rodaClone.driver.drawer.DrawerActivity
import com.rodaClone.driver.ut.SessionMaintainence
import javax.inject.Inject

class RatingFragment : BaseFragment<FragmentRatingBinding, RatingVM>(),
    RatingNavigator , OnMapReadyCallback {
    companion object {
        const val TAG = "RatingFragment"
    }

    lateinit var _binding: FragmentRatingBinding
    private var latLng = ObservableField<LatLng>()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    val vm by lazy {
        ViewModelProvider(this, viewModelFactory).get(RatingVM::class.java)
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
        _binding = getmBinding()
        vm.setNavigator(this)
        arguments?.let {
            vm.requestData = it.getSerializable("REQUEST_DATA") as? RequestResponseData
        }
        vm.setData()
        initializeMapAndLocation()

    }

    private fun initializeMapAndLocation() {
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.rating_fragment) as? SupportMapFragment
        mapFragment!!.getMapAsync(this)
        vm.fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

    }


    override fun getLayoutId() = R.layout.fragment_rating

    override fun getBR() = BR.viewModel

    override fun getVMClass() = vm

    fun closeFragment() {
        findNavController().popBackStack()
    }

    override fun goToHome() {
        if(isAdded)
        (requireActivity() as DrawerActivity).navigateFirstTabWithClearStack()
    }

    override fun onMapReady(p0: GoogleMap) {
        if (vm.session.getString(SessionMaintainence.CURRENT_LATITUDE)?.isNotEmpty() == true &&
            vm.session.getString(SessionMaintainence.CURRENT_LONGITUDE)?.isNotEmpty() == true
        ) {
            latLng.set(vm.session.getString(SessionMaintainence.CURRENT_LATITUDE)
                ?.let {
                    LatLng(
                        it.toDouble(),
                        vm.session.getString(SessionMaintainence.CURRENT_LONGITUDE)!!.toDouble()
                    )
                })
        }
        latLng.get()?.let { vm.moveCamera(p0, it, 16f) }
    }


}