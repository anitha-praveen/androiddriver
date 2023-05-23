package com.rodaClone.driver.dialogs.takeMeterPhoto

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.rodaClone.driver.BR
import com.rodaClone.driver.R
import com.rodaClone.driver.base.BaseDialog
import com.rodaClone.driver.databinding.TakeMeterPhotoLayoutBinding
import com.rodaClone.driver.ut.Utilz
import java.util.*
import javax.inject.Inject

//mode 1-> for end trip to check start km is greater than end trip

class TakeMeterPhotoDialog(private val mode:Int,private val km:Int) : BaseDialog<TakeMeterPhotoLayoutBinding, TakeMeterPhotoVm>(),
    TakeMeterPhotoNavigator {
    companion object {
        const val TAG = "TakeMetePhotoDialog"
    }

    /*
  The below variables and methods are declared for handling camera or gallery permissions and get data from corresponding selection
   */
    private lateinit var binding: TakeMeterPhotoLayoutBinding

    @Inject
    lateinit var vmProviderFactory: ViewModelProvider.Factory
    private val vm by lazy {
        ViewModelProvider(this, vmProviderFactory).get(TakeMeterPhotoVm::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getmBinding()
        vm.setNavigator(this)
        binding.backImg.setOnClickListener { dismiss() }
        setFullSCreen()
        vm.reckms.set(km)
        vm.mode.set(mode)
    }


    private var resultLauncherCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                vm.onCaptureImageResult(data!!)
            }
        }
    /*
     The below declaration receive the result of permission allowed or not
      */

    val requestPermissionCamera =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                resultLauncherCamera.launch(intent)
            } else {
                // Explain to the user that the feature is unavailable because the
                // features requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
            }
        }


    override fun getLayout(): Int = R.layout.take_meter_photo_layout


    override fun getBindingVariable(): Int = BR.viewModel


    override fun getViewModel(): TakeMeterPhotoVm = vm
    override fun openCamera() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionCamera.launch(Manifest.permission.CAMERA)
        } else {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            resultLauncherCamera.launch(intent)
        }
    }

    fun setFullSCreen() {
        if (dialog!!.window != null) {
            dialog!!.window!!.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    mActivity as Context,
                    R.color.clr_trans
                )
            )
            dialog!!.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        dialog!!.setCanceledOnTouchOutside(false)
    }

    override fun getCtx(): Context? {
        return context
    }


    override fun dissmissFrag() {
        dismiss()
    }


    override fun showSnackBar(message: String) =
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()

    override fun isNetworkConnected(): Boolean = Utilz.checkForInternet(requireActivity())

}