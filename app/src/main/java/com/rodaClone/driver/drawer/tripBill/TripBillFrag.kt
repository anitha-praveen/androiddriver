package com.rodaClone.driver.drawer.tripBill

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.rodaClone.driver.BR
import com.rodaClone.driver.R
import com.rodaClone.driver.base.BaseFragment
import com.rodaClone.driver.connection.responseModels.RequestResponseData
import com.rodaClone.driver.databinding.TripBillLayBinding
import com.rodaClone.driver.drawer.DrawerActivity
import com.rodaClone.driver.ut.Utilz
import javax.inject.Inject

class TripBillFrag : BaseFragment<TripBillLayBinding, TripBillVM>(),
    TripBillNavigator {

    lateinit var _binding: TripBillLayBinding


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    val vm by lazy {
        ViewModelProvider(this, viewModelFactory).get(TripBillVM::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (vm.mode.get() != 0)
                    closeFragment()
            }
        })
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = getmBinding()
        vm.setNavigator(this)

        arguments?.let { it ->
            vm.requestData = it.getSerializable("REQUEST_DATA") as? RequestResponseData
            vm.currency.set(vm.requestData?.requestBill?.billData?.requestedCurrencySymbol)
            vm.mode.set(it.getInt("MODE"))
            vm.requestData?.paymentOpt.let { pay_type ->
//                if (pay_type == "CARD") {
//                    vm.isContinueEnabled.set(false)
//                    vm.cashDescription.set(vm.translationModel?.txt_wait_until_user_pay_complete)
//                }
//                else {
                    vm.isContinueEnabled.set(true)
                    vm.cashDescription.set(vm.translationModel?.txt_collect_cash)
//                }
            }

            vm.requestData?.requestBill?.billData?.totalAmount?.let {
                vm.totaltAmount.set("${vm.currency.get()} ${Utilz.removeZero("$it")}")
                vm.tripAmount.set(it.toInt())
            }
        }
    }


    override fun getLayoutId() = R.layout.trip_bill_lay

    override fun getBR() = BR.viewModel

    override fun getVMClass() = vm

    fun closeFragment() {
        findNavController().popBackStack()
    }

    override fun sos() {
        findNavController().navigate(R.id.support_to_sos)
    }

    override fun onclickConfirm() {
        if (vm.mode.get() == 0) {
            if (vm.requestData?.is_instant_trip == 1)
                (requireActivity() as DrawerActivity).navigateFirstTabWithClearStack()
            else {
                val bundle = bundleOf("REQUEST_DATA" to vm.requestData)
                findNavController().navigate(R.id.invoice_to_rating, bundle)
            }

            /**
             * delete the request node in firebase once the trip was end.
             */
//            requestRef.child(vm.requestData!!.id!!)
//                .removeValue()


        } else
            findNavController().popBackStack()
    }




}