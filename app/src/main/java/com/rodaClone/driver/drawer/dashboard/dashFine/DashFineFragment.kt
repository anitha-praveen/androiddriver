package com.rodaClone.driver.drawer.dashboard.dashFine

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.rodaClone.driver.BR
import com.rodaClone.driver.R
import com.rodaClone.driver.base.BaseFragment
import com.rodaClone.driver.connection.responseModels.DashboardModel
import com.rodaClone.driver.databinding.FragmentDashFinesBinding
import javax.inject.Inject

class DashFineFragment(private val dashboardModel: DashboardModel?) : BaseFragment<FragmentDashFinesBinding, DashFineVM>(),
    DashFineNavigator {

    lateinit var _binding: FragmentDashFinesBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    val vm by lazy {
        ViewModelProvider(this, viewModelFactory).get(DashFineVM::class.java)
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
        setValues()
    }


    override fun getLayoutId() = R.layout.fragment_dash_fines

    override fun getBR() = BR.viewModel

    override fun getVMClass() = vm

    fun closeFragment(){
        findNavController().popBackStack()
    }

    fun setValues(){
        dashboardModel?.fineAmount?.let {
            vm.fineAmnt.set(it)
            vm.fineVisible.set(true)
        }
        dashboardModel?.cancellationFeeAmount?.let { vm.captainAmnt.set(it) }
        dashboardModel?.cancellationEarnAmount?.let { vm.customerAmnt.set(it) }
        dashboardModel?.balanceCancellationAmount?.let { vm.netAmnt.set(it) }
//        var fineReason = ObservableField("")
//        var bonusAmnt = ObservableField("")
//        var bonusDesc = ObservableField("")
//        var netProfitAmnt = ObservableField("")
//        var bonusVisible = ObservableBoolean(false)
    }

}