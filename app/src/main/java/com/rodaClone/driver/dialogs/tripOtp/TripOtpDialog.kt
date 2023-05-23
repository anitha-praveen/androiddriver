package com.rodaClone.driver.dialogs.tripOtp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.rodaClone.driver.BR
import com.rodaClone.driver.R
import com.rodaClone.driver.base.BaseDialog
import com.rodaClone.driver.databinding.DialogTripOtpBinding
import com.rodaClone.driver.ut.Config
import com.rodaClone.driver.ut.Utilz
import javax.inject.Inject

class TripOtpDialog : BaseDialog<DialogTripOtpBinding, TripOtpVM>(),
    TripOtpNavigator {
    companion object {
        const val TAG = "TripOtpDialog"
    }

    private lateinit var binding: DialogTripOtpBinding


    @Inject
    lateinit var vmProviderFactory: ViewModelProvider.Factory
    private val vm by lazy {
        ViewModelProvider(this, vmProviderFactory).get(TripOtpVM::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getmBinding()
        vm.setNavigator(this)
        setDialogFitWidth()
        setClearBackground()
    }

    override fun getLayout(): Int = R.layout.dialog_trip_otp


    override fun getBindingVariable(): Int = BR.viewModel

    override fun getViewModel(): TripOtpVM = vm

    override fun confirm() {
        if(binding.tripOtpView.oTP.length < 4 )
            showSnackBar("Enter complete opt")
        else{
            val intent = Intent(Config.RECEIVE_TRIP_OTP)
            intent.putExtra(Config.otp, binding.tripOtpView.oTP)
            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
            binding.tripOtpView.setOTPCustom("")
            dismiss()
        }
    }

    override fun cancel() {
        binding.tripOtpView.setOTPCustom("")
        dismiss()
    }


    override fun showSnackBar(message: String) =
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()

    override fun isNetworkConnected(): Boolean = Utilz.checkForInternet(requireActivity())
}