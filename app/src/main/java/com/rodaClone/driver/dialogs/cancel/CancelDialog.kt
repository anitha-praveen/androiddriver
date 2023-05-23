package com.rodaClone.driver.dialogs.cancel

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.rodaClone.driver.BR
import com.rodaClone.driver.R
import com.rodaClone.driver.base.BaseDialog
import com.rodaClone.driver.connection.responseModels.AvailableCountryAndKLang
import com.rodaClone.driver.connection.responseModels.Country
import com.rodaClone.driver.ut.Utilz
import javax.inject.Inject
import androidx.localbroadcastmanager.content.LocalBroadcastManager

import android.content.Intent
import com.google.gson.Gson
import com.rodaClone.driver.connection.BaseResponse
import com.rodaClone.driver.connection.responseModels.RequestResponseData
import com.rodaClone.driver.databinding.CancelDialogBinding
import com.rodaClone.driver.drawer.DrawerActivity
import com.rodaClone.driver.ut.Config

class CancelDialog: BaseDialog<CancelDialogBinding, CancelListVM>(),
    CancelListNavigator {
    companion object {
        const val TAG = "CountryListDialog"
    }

    private lateinit var binding: CancelDialogBinding
    var cancelListAdapter: CancelListAdapter? = null
    var linearLayoutManager: LinearLayoutManager? = null
    lateinit var data: AvailableCountryAndKLang

    @Inject
    lateinit var vmProviderFactory: ViewModelProvider.Factory
    private val vm by lazy {
        ViewModelProvider(this, vmProviderFactory).get(CancelListVM::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getmBinding()
        vm.setNavigator(this)
        setDialogFullSCreen()
        vm.cancelationApi()
        if (arguments != null) {
            val mArgs = arguments
            val requestData = mArgs!!.getString(Config.requestData)
            val driverStatus = mArgs.getBoolean(Config.isArrivedStatus)
            vm.requestDataaa = Gson().fromJson(requestData, RequestResponseData::class.java)
            vm.driverStatus!!.set(driverStatus)
        }

    }

    override fun getLayout(): Int = R.layout.cancel_dialog


    override fun getBindingVariable(): Int = BR.viewModel


    override fun getViewModel(): CancelListVM = vm
    override fun clickedItem(country: Country) {
        val intent = Intent(Config.RECEIVE_COUNTRY)
        intent.putExtra(Config.country, country)
        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
        dismiss()
    }

    override fun dismissDialog() {
        dismiss()
    }

    override fun loadReasons(reasons: List<BaseResponse.CancelReason>?) {
        linearLayoutManager = LinearLayoutManager(activity)
        cancelListAdapter = CancelListAdapter(requireActivity(), reasons!!, this)
        binding.recyclerReason.layoutManager = linearLayoutManager
        binding.recyclerReason.adapter = cancelListAdapter
    }

    override fun getSelectedPosition(): Int {
        return cancelListAdapter?.getChoosedPosition() ?: -1
    }

    override fun closeTripFragment() {
        (mActivity as DrawerActivity).navigateFirstTabWithClearStack()
        dismissDialog()
    }

    override fun getCtx(): Context {
        return requireContext()
    }


    override fun showSnackBar(message: String) =
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()

    override fun isNetworkConnected(): Boolean = Utilz.checkForInternet(requireActivity())

}