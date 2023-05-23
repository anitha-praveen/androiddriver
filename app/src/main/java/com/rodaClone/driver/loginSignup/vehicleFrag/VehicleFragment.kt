package com.rodaClone.driver.loginSignup.vehicleFrag

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rodaClone.driver.BR
import com.rodaClone.driver.MyApplication
import com.rodaClone.driver.R
import com.rodaClone.driver.base.BaseFragment
import com.rodaClone.driver.connection.responseModels.AvailableCountryAndKLang
import com.rodaClone.driver.connection.responseModels.Vehicles
import javax.inject.Inject
import com.rodaClone.driver.databinding.VehicleIntroFragBinding
import com.rodaClone.driver.drawer.DrawerActivity
import com.rodaClone.driver.loginSignup.CustomSpinnerAdapter
import com.rodaClone.driver.loginSignup.vehicleFrag.model.ServiceTypes
import com.rodaClone.driver.loginSignup.vehicleFrag.model.SignupTypeData
import com.rodaClone.driver.ut.Utilz
import kotlin.collections.ArrayList


class VehicleFragment : BaseFragment<VehicleIntroFragBinding, VehicleFragVM>(),
    VehicleNavigator {
    companion object {
        const val TAG = "VehicleFragment"
    }

    private lateinit var binding: VehicleIntroFragBinding
    lateinit var data: AvailableCountryAndKLang
    var dialog: Dialog? = null

    @Inject
    lateinit var vmProviderFactory: ViewModelProvider.Factory
    private val vm by lazy {
        ViewModelProvider(this, vmProviderFactory).get(VehicleFragVM::class.java)
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
        vm.typesApiCall()

//        Log.e("Result", SignupData.fname)

        binding.backImg.setOnClickListener { closeFragment() }
    }


    override fun getLayoutId() = R.layout.vehicle_intro_frag

    override fun getBR() = BR.viewModel

    override fun getVMClass() = vm

    val handler = Handler(Looper.getMainLooper())
    private val showR : Runnable = Runnable {
        binding.modelName.isFocusableInTouchMode = true;
        binding.modelName.requestFocus()
        if(isAdded)
            Utilz.openKeyBoard(requireActivity())
    }

    private val hideR : Runnable = Runnable {
        if (isAdded)
            Utilz.hideKeyboard(requireContext(),binding.root)
    }

    override fun hideKeyBoard() {
        handler.postDelayed(hideR,200)
    }

    override fun openKeyBoard() {
        handler.postDelayed(showR,200)
    }

    override fun getContextAttached(): Context {
        return requireContext()
    }

    /*
    The below method loadDataAdapter called when types api called
     */
    override fun loadDataAdapter(signupTypesList: ArrayList<SignupTypeData?>) {
        val s_adapter = SignupTypesAdapter(requireActivity(), signupTypesList, this)
        binding.typesRecyler.adapter = s_adapter
        binding.typesRecyler.layoutManager =
            LinearLayoutManager(
                requireContext(), LinearLayoutManager.HORIZONTAL, false
            )
    }


    /*
    This called when vehicle type selected from SignupTypesAdapter
     */
    override fun vehicleSlug(data: SignupTypeData) {
        vm.selectedVehicleTypeData = data
        vm.selectedVehicleTypeData.slug?.let { vm.getVehicleModels(it) }
        vm.isAnyVehicleTypeChosen.set(true)
        clearLocalSelections()
        if (vm.selectedVehicleTypeData.serviceTypes != null)
            vm.showSelectService.set(true)
        else {
            vm.showSelectService.set(false)
            /* If no options given from backend by default LOCAL is sent to backend in submit api*/
            vm.selectedServices.set("LOCAL")
        }
    }

    override fun getToHome() {
        startActivity(Intent(requireContext(), DrawerActivity::class.java))
    }

    override fun getAct(): Context {
        return requireActivity()
    }

    /*
    The below selectServiceTypes is open when select service type clicked
    For eg: To select options like LOCAL,OUTSTATION,RENTAL
     */
    private var localSelectedList: MutableList<ServiceTypes> = ArrayList()
    override fun selectServiceTypes() {
        dialog = Dialog(requireContext())
        dialog?.setContentView(R.layout.dialog_select_serivice_layout)
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        var list: MutableList<ServiceTypes> = ArrayList()
        vm.selectedVehicleTypeData.serviceTypes?.let { serviceTypes ->
            if (localSelectedList.isEmpty()) {
                for (data in serviceTypes) {
                    val service = ServiceTypes()
                    service.text = data
                    list.add(service)
                }
            } else {
                list = localSelectedList
            }
            val text: TextView = dialog?.findViewById(R.id.title)!!
            text.text = vm.translationModel?.txt_select_all_services
            val recyclerView: RecyclerView = dialog?.findViewById(R.id.select_service_recycler)!!
            recyclerView.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            val adapter = ServiceCategoryAdapter(
                list,
                if (isAdded) requireContext() else MyApplication.appContext!!
            )
            recyclerView.adapter = adapter
            val button: Button = dialog?.findViewById(R.id.submit)!!
            button.text = vm.translationModel?.text_submit
            button.setOnClickListener {
                adapter.mModelList?.let {
                    localSelectedList = it
                    var text = ""
                    for (item in it) {
                        item.text?.let { value ->
                            if (item.isSelected) {
                                text = if (text.isEmpty())
                                    value.uppercase()
                                else
                                    "$text, ${value.uppercase()}"
                            }
                        }
                    }
                    vm.selectedServices.set(text)
                    if (vm.selectedServices.get()?.isEmpty()!!) {
                        showMessage(vm.translationModel?.text_Please_Select_an_Option?:"")
                    } else{
                        dialog?.dismiss()
                        hideKeyBoard()
                    }
                }
            }
            dialog?.show()
        }
    }

    /*
    The below method clearLocalSelections is called when a vehicle tye is selected
     */
    override fun clearLocalSelections() {
        localSelectedList.clear()
        vm.selectedServices.set("")
        vm.selectedVehicleModel.set("")
        vm.selectedVehicleModelSlug = ""
        vm.showOtherVehicleModelOptions.set(false)
        vm.edit_vehiclenumber_register.set("")
        vm.modelName.set("")
    }


    /*
    The below method vehicleModelSelection is to select vehicle model
     */
    var vehicleModelDialog: Dialog? = null
    var customList: MutableList<Vehicles.Vehiclemodel> = ArrayList()
    override fun vehicleModelSelection(modelsList: List<Vehicles.Vehiclemodel>) {
        vehicleModelDialog = Dialog(requireContext())
        vehicleModelDialog?.setContentView(R.layout.spinner_like_layout)
        vehicleModelDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        var customClass = Vehicles.Vehiclemodel()
        customClass.modelName = "OTHER"
        customClass.slug = "OTHER"
        var lastIndex = 0
        customList.clear()
        modelsList.forEachIndexed { index, company ->
            customList.add(company)
            lastIndex = index
        }
        customList.add(lastIndex + 1, customClass)
        val recyclerView: RecyclerView = vehicleModelDialog!!.findViewById(R.id.spinner_recycler)
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val serviceLocAdapter = CustomSpinnerAdapter(customList, this, vm.selectedVehicleModelSlug)
        recyclerView.adapter = serviceLocAdapter
        vehicleModelDialog?.show()

    }

    override fun setSelectedVehicleModel(vehicleModel: Vehicles.Vehiclemodel) {
        vehicleModelDialog?.dismiss()
        if (vehicleModel.slug == "OTHER") {
            vm.showOtherVehicleModelOptions.set(true)
            openKeyBoard()
        } else {
            vm.showOtherVehicleModelOptions.set(false)
            hideKeyBoard()
        }
        vm.selectedVehicleModelSlug = vehicleModel.slug ?: ""
        vm.selectedVehicleModel.set(vehicleModel.modelName ?: "")
    }

    override fun getBinds(): VehicleIntroFragBinding {
        return binding
    }


    private fun closeFragment() {
        findNavController().popBackStack()
    }

}