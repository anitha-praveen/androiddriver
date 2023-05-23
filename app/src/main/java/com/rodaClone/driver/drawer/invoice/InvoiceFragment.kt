package com.rodaClone.driver.drawer.invoice

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.database.FirebaseDatabase
import com.rodaClone.driver.BR
import com.rodaClone.driver.R
import com.rodaClone.driver.base.BaseFragment
import com.rodaClone.driver.databinding.FragmentInvoiceBinding
import com.rodaClone.driver.ut.Config
import javax.inject.Inject

class InvoiceFragment : BaseFragment<FragmentInvoiceBinding, InvoiceVM>(),
    InvoiceNavigator {

    lateinit var _binding: FragmentInvoiceBinding
    var database = FirebaseDatabase.getInstance()
    var requestRef = database.getReference("requests")
   // val args:InvoiceFragmentArgs by navArgs()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    val vm by lazy {
        ViewModelProvider(this, viewModelFactory).get(InvoiceVM::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (vm.mode != 0)
                    closeFragment()
            }
        })

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = getmBinding()
        vm.setNavigator(this)
        _binding.backImg.setOnClickListener {
            if (vm.mode != 0)
                closeFragment()
        }
        arguments?.let {
            vm.recReqId.set(it.getString(Config.request_id))
            vm.mode = it.getInt(Config.mode)
        }

       // vm.setupInitialData()

        vm.callSingleHistoryApi()
    }


    override fun getLayoutId() = R.layout.fragment_invoice

    override fun getBR() = BR.viewModel

    override fun getVMClass() = vm

    fun closeFragment() {
        findNavController().popBackStack()
    }

    override fun chooseDirection() {
        if (vm.mode == 0) {
            if (vm.requestData?.is_instant_trip == 1)
                findNavController().navigate(R.id.invoice_to_map)
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
            findNavController().navigateUp()

    }

    override fun gotoComplaints() {
        val bundle = bundleOf(Config.mode to 1, Config.request_id to vm.requestData?.id)
        findNavController().navigate(R.id.invoice_to_complaints,bundle)
    }


}