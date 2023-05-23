package com.rodaClone.driver.drawer.vehicleInformation

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.rodaClone.driver.BR
import com.rodaClone.driver.R
import com.rodaClone.driver.base.BaseFragment
import com.rodaClone.driver.connection.responseModels.DriverModel
import com.rodaClone.driver.databinding.FragmentVehicleInfoBinding
import javax.inject.Inject

class VehicleInfoFragment: BaseFragment<FragmentVehicleInfoBinding, VehicleInfoVM>(),
    VehicleInfoNavigator {

    lateinit var _binding: FragmentVehicleInfoBinding

    @Inject
    lateinit var viewModelFagtory: ViewModelProvider.Factory
    val vm by lazy {
        ViewModelProvider(this, viewModelFagtory).get(VehicleInfoVM::class.java)
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
        _binding.backImg.setOnClickListener { closeFragment() }
        vm.carDetails = arguments?.getSerializable("vehicleInfo") as DriverModel.CarDetails
        vm.setUpDetails()
    }

    override fun getLayoutId() = R.layout.fragment_vehicle_info

    override fun getBR() = BR.viewModel

    override fun getVMClass() = vm

    fun closeFragment(){
        findNavController().popBackStack()
    }

}