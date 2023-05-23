package com.rodaClone.driver.drawer.appStatus

import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.FirebaseDatabase
import com.rodaClone.driver.BR
import com.rodaClone.driver.BuildConfig
import com.rodaClone.driver.R
import com.rodaClone.driver.base.BaseFragment
import com.rodaClone.driver.databinding.AppStatusFragBinding
import com.rodaClone.driver.drawer.DrawerActivity
import com.rodaClone.driver.ut.SessionMaintainence
import javax.inject.Inject


class AppStatusFrag : BaseFragment<AppStatusFragBinding, AppStatusVm>(),
    AppStatusNavigator {
    lateinit var binding: AppStatusFragBinding
    var database: FirebaseDatabase

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    val vm by lazy {
        ViewModelProvider(this, viewModelFactory).get(AppStatusVm::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                closeFragment()
            }
        })
        vm.appVersion.set("${vm.translationModel?.txt_app_version}: V ${BuildConfig.VERSION_NAME}")
    }

    init {
        database = FirebaseDatabase.getInstance()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getmBinding()
        binding.backImg.setOnClickListener { closeFragment() }
        vm.setNavigator(this)
        vm.ref = database.getReference("drivers")
        if (vm.session.getBoolean(SessionMaintainence.DriverStatus)) {
            vm.lastUpdatedTxt.set("${vm.translationModel?.txt_last_updated}: ")
            vm.getUpdatedMills()
        }
        else {
            vm.showLastUpdatedTxt.set(false)
            vm.lastUpdatedTxt.set(vm.translationModel?.txt_you_offline)
            vm.isUpdatedOk.set(true)
            vm.updateAppText()
        }
        vm.getLocationAndNetwork(context)
        vm.checkBatteryOptimization(context)
        val deviceName = Build.MANUFACTURER
        if (deviceName.equals("vivo")){
            vm.showAutoStart.set(true)
        }
        else
            vm.showAutoStart.set(false)


    }

    private var resultBatteryOptimization =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}


    override fun getLayoutId() = R.layout.app_status_frag

    override fun getBR() = BR.viewModel

    override fun getVMClass() = vm

    fun closeFragment() {
        if (vm.session.getString(SessionMaintainence.ReqID)?.isEmpty() == true)
            findNavController().popBackStack()
        else
            (requireActivity() as DrawerActivity).reqProgress()

    }

    override fun openLocationSetting() {
        activity?.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }

    override fun openBatteryOptimization() {
        resultBatteryOptimization.launch(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS))
    }

    override fun callReqinPro() {
        (requireActivity() as DrawerActivity).reqProgress()
    }

    override fun openAutoStart() {
        if (Build.MANUFACTURER == "vivo" ){
            try {
                val intent = Intent()
                intent.component = ComponentName(
                    "com.iqoo.secure",
                    "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity"
                )
                this.startActivity(intent)
            } catch (e: Exception) {
                try {
                    val intent = Intent()
                    intent.component = ComponentName(
                        "com.vivo.permissionmanager",
                        "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
                    )
                    this.startActivity(intent)
                } catch (ex: Exception) {
                    try {
                        val intent = Intent()
                        intent.setClassName(
                            "com.iqoo.secure",
                            "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager"
                        )
                        this.startActivity(intent)
                    } catch (exx: Exception) {
                        ex.printStackTrace()
                    }
                }
            }
        }
    }

    fun autoStartOppo(){
        try {
            val intent = Intent()
            intent.setClassName(
                "com.coloros.safecenter",
                "com.coloros.safecenter.permission.startup.StartupAppListActivity"
            )
            startActivity(intent)
        } catch (e: java.lang.Exception) {
            try {
                val intent = Intent()
                intent.setClassName(
                    "com.oppo.safe",
                    "com.oppo.safe.permission.startup.StartupAppListActivity"
                )
                startActivity(intent)
            } catch (ex: java.lang.Exception) {
                try {
                    val intent = Intent()
                    intent.setClassName(
                        "com.coloros.safecenter",
                        "com.coloros.safecenter.startupapp.StartupAppListActivity"
                    )
                    startActivity(intent)
                } catch (exx: java.lang.Exception) {
                }
            }
        }
    }


}