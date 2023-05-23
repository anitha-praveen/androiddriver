package com.rodaClone.driver.drawer.wallet

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rodaClone.driver.BR
import com.rodaClone.driver.R
import com.rodaClone.driver.base.BaseFragment
import com.rodaClone.driver.connection.BaseResponse
import com.rodaClone.driver.connection.responseModels.CustomWalletModel
import com.rodaClone.driver.databinding.FragmentWalletBinding
import com.rodaClone.driver.drawer.DrawerActivity
import com.rodaClone.driver.drawer.wallet.adapter.WalletHistoryAdapter
import com.rodaClone.driver.ut.Config
import com.rodaClone.driver.ut.SessionMaintainence
import javax.inject.Inject

class WalletFragment: BaseFragment<FragmentWalletBinding, WalletVm>(),
    WalletNavigator {

    lateinit var binding: FragmentWalletBinding


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    val vm by lazy {
        ViewModelProvider(this, viewModelFactory).get(WalletVm::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() { closeFragment()} })

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getmBinding()
        vm.setNavigator(this)
        binding.backImg.setOnClickListener { closeFragment() }
        vm.getListApi()
    }




    override fun getLayoutId() = R.layout.fragment_wallet

    override fun getBR() = BR.viewModel

    override fun getVMClass() = vm

    fun closeFragment(){
        if (vm.session.getString(SessionMaintainence.ReqID)?.isEmpty() == true)
            findNavController().popBackStack()
        else
            (requireActivity() as DrawerActivity).reqProgress()
    }

    override fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun setUpAdapter(list: ArrayList<CustomWalletModel>,type: BaseResponse.DataObjectsAllApi) {
        val layoutManager = LinearLayoutManager(context)
        val adapter = WalletHistoryAdapter(list,type,this)
        binding.walletRe.layoutManager = layoutManager
        binding.walletRe.adapter = adapter
    }

    override fun openInvoice(reqId: String) {
        val bundle = bundleOf(Config.request_id to reqId)
//        val action = WalletFragmentDirections.walletToInvoice(
//            reqId = reqId,
//            mode = 1
//        )
        findNavController().navigate(R.id.wallet_to_invoice,bundle)
    }


}