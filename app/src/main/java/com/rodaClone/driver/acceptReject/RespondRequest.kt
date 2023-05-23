package com.rodaClone.driver.acceptReject

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.ncorti.slidetoact.SlideToActView
import com.rodaClone.driver.BR
import com.rodaClone.driver.R
import com.rodaClone.driver.base.BaseFragment
import com.rodaClone.driver.connection.responseModels.AvailableCountryAndKLang
import com.rodaClone.driver.connection.responseModels.RequestResponseData
import com.rodaClone.driver.databinding.AcceptRejectLayBinding
import com.rodaClone.driver.drawer.DrawerActivity
import com.rodaClone.driver.drawer.trip.TripObject
import com.rodaClone.driver.ut.Config
import javax.inject.Inject


class RespondRequest : BaseFragment<AcceptRejectLayBinding, RespondRequestVM>(),
    RespondRequestNav {

    private lateinit var binding: AcceptRejectLayBinding
    lateinit var data: AvailableCountryAndKLang

    @Inject
    lateinit var vmProviderFactory: ViewModelProvider.Factory

    val vm by lazy {
        ViewModelProvider(this, vmProviderFactory).get(RespondRequestVM::class.java)
    }

    //to close accept reject when push received
    private val closeAcceptReject: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            if (findNavController().currentDestination?.label != getString(R.string.txt_Home))
                (requireActivity() as DrawerActivity).navigateFirstTabWithClearStack()
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getmBinding()
        vm.setNavigator(this)
        arguments?.let {
            val json = it.getString(Config.REQ_DATA)
            json?.let { reqJson ->
                val responseData: RequestResponseData =
                    Gson().fromJson(reqJson, RequestResponseData::class.java)
                vm.requestDetails(responseData)
            }
        }
        binding.slideToCancel.onSlideCompleteListener =
            (object : SlideToActView.OnSlideCompleteListener {
                override fun onSlideComplete(view: SlideToActView) {
                    vm.acceptRejectClicked(0)
                }

            })
    }


    override fun onDestroy() {
        super.onDestroy()
        vm.mainHandler.removeCallbacks(vm.progressRunner)
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(closeAcceptReject, IntentFilter(Config.CLOSE_ACCEPTREJECT))
        vm.playSound()
        TripObject.isAcceptRejectVisible = true

    }

    override fun onPause() {
        TripObject.isAcceptRejectVisible = false
        vm.stopSound()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(closeAcceptReject)
        super.onPause()
    }

    override fun getLayoutId() = R.layout.accept_reject_lay
    override fun getBR() = BR.viewModel
    override fun getVMClass() = vm


    override fun getCtx(): Context {
        return requireContext()
    }

    override fun updateProgressBar(progress: Int) {
        binding.progressBar.progress = progress
    }

    override fun setMax(max: Int) {
        binding.progressBar.max = max
    }

    override fun callReqProgress() {
        (requireActivity() as DrawerActivity).reqProgress()
    }
}