package com.rodaClone.driver.drawer

import android.app.Activity
import android.app.Dialog
import android.app.NotificationManager
import android.content.*
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.LocationManager
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.service.notification.StatusBarNotification
import android.util.Log
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavArgument
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.work.Constraints
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.ActivityResult.RESULT_IN_APP_UPDATE_FAILED
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.gson.Gson
import com.rodaClone.driver.BR
import com.rodaClone.driver.BuildConfig
import com.rodaClone.driver.MyApplication
import com.rodaClone.driver.R
import com.rodaClone.driver.base.BaseAppActivity
import com.rodaClone.driver.connection.BaseResponse
import com.rodaClone.driver.connection.responseModels.RequestResponseData
import com.rodaClone.driver.databinding.ActivityDrawerBinding
import com.rodaClone.driver.databinding.NavHeaderDrawerBinding
import com.rodaClone.driver.drawer.optimized_dialog.OptimizingDialog
import com.rodaClone.driver.loginSignup.SignupActivity
import com.rodaClone.driver.ut.Config
import com.rodaClone.driver.ut.FloatingService
import com.rodaClone.driver.ut.MyWorkerClass
import com.rodaClone.driver.ut.SessionMaintainence
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


open class DrawerActivity : BaseAppActivity<ActivityDrawerBinding, DrawerVM>(), DrawerNavigator,
    NavigationView.OnNavigationItemSelectedListener {
    companion object {
        const val TAG = "DrawerActivity"
        const val ACTION_STOP_FOREGROUND = "${BuildConfig.APPLICATION_ID}.stopfloating.service"
        var isDrawer = false
    }

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityDrawerBinding
    lateinit var navController: NavController
    var mDrawer: DrawerLayout? = null
    private var appUpdateManager: AppUpdateManager? = null

    //binding service
    private lateinit var mService: FloatingService
    private var mBound: Boolean = false

    //to change the bubble state
    var bubbleState = false


    @Inject
    lateinit var vmProviderFactory: ViewModelProvider.Factory
    val vm by lazy {
        ViewModelProvider(this, vmProviderFactory).get(DrawerVM::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) /* This will prevent app going to sleep while in this activity */
        binding = getmBinding()
        vm.setNavigator(this)
        if (!vm.session.getString(SessionMaintainence.CURRENT_LANGUAGE).isNullOrEmpty()) {
            vm.setLanguage(this, vm.session.getString(SessionMaintainence.CURRENT_LANGUAGE)!!)
        }else{
            vm.setLanguage(this,"en")
        }

        //initListeners()
        appUpdateManager = AppUpdateManagerFactory.create(this)
        setup()
        binding.switchCompat.setOnCheckedChangeListener { buttonView, isChecked ->
            if (buttonView.isPressed)
                vm.toggleStatus()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(logoutPushReceiver)
    }


    private lateinit var navHostFragment: NavHostFragment
    private fun setup() {
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        navController = navHostFragment.navController

        findViewById<NavigationView>(R.id.nav_view)
            .setupWithNavController(navController)
        appBarConfiguration = AppBarConfiguration(navController.graph, binding.drawerLayout)
        mDrawer = binding.drawerLayout
        val bundle = Bundle()
        bundle.putString(Config.vehicle_type_slug, vm.typeSlug.get())
        navHostFragment.arguments = bundle
        mDrawer!!.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                vm.showProfilePic.set(false)
            }

            override fun onDrawerOpened(drawerView: View) {
                vm.showProfilePic.set(false)
            }

            override fun onDrawerClosed(drawerView: View) {
                checkDrawer()
            }

            override fun onDrawerStateChanged(newState: Int) {
                if (!mDrawer!!.isOpen)
                    checkDrawer()

            }
        })

        setUpNavigationHeader()
        setUpNavMenuTranslation()
        reqProgress()
//        checkUpdate()
    }

    fun reqProgress() {
        vm.requestInProgressApi()
    }

    fun updateProfileInfo(fName: String, lName: String, driverProfile: String) {
        vm.updateDriverProfile(fName, lName, driverProfile)
    }


//    private val profilePicUpdated: BroadcastReceiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context, intent: Intent) {
//            reqProgress()
//        }
//    }

    /**
     * update location to firebase database.
     */
//    private val locationUpdate: BroadcastReceiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context, intent: Intent) {
////            if (checkIfLocationEnabled()) {
////                vm.locationUpdate(context)
////            } else {
////                showEnableLocation()
////            }
//            if(!checkIfLocationEnabled()){
//                showEnableLocation()
//            }
//        }
//    }


    private fun checkDrawer() {
        if (navController.currentDestination != null)
            if (navController.currentDestination!!.label == "Home") {
                vm.showProfilePic.set(true)
            }

    }


    lateinit var navHeaderDrawerBinding: NavHeaderDrawerBinding
    private fun setUpNavigationHeader() {
        navHeaderDrawerBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.nav_header_drawer, binding.navView, false
        )
        binding.navView.addHeaderView(navHeaderDrawerBinding.root)
        navHeaderDrawerBinding.viewModel = vm
        binding.navView.setNavigationItemSelectedListener(this)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            vm.showProfilePic.set(destination.id == R.id.map_fragment)
        }
    }

    override fun getLayoutId() = R.layout.activity_drawer

    override fun getBR() = BR.viewModel

    override fun getVMClass() = vm


    override fun openSideMenu() {
        if (!mDrawer!!.isDrawerOpen(GravityCompat.START)) mDrawer!!.openDrawer(GravityCompat.START)
    }

    override fun setImage(url: String) {
        if(isDrawer){
            Glide.with(this).load(url).apply(
                RequestOptions.circleCropTransform().error(R.drawable.ic_user)
                    .placeholder(R.drawable.ic_user)
            ).into(navHeaderDrawerBinding.imageView)

            Glide.with(this).load(url).apply(
                RequestOptions.circleCropTransform().error(R.drawable.ic_user)
                    .placeholder(R.drawable.ic_user)
            ).into(binding.menuHam)
        }
    }

    override fun openAcceptRejectAct(data: RequestResponseData?) {
        runOnUiThread {
            if(isDrawer){
                navController.currentDestination?.label?.let { label ->
                    Log.e(TAG, "myLabel : $label", )
                    if (label != getString(R.string.label_respond_request) && label != "Trip Frag") {
                        vm.session.clearTripDetails()
                        val bundle = bundleOf(Config.REQ_DATA to Gson().toJson(data))
                        navController.navigate(R.id.nav_respond_request, bundle)
                    }
                }
            }
        }

    }


    override fun openTripData(data: RequestResponseData?, driver: BaseResponse.ReqDriverData) {

        navController.currentDestination?.label.let {
            if(it != "Trip Frag"){
                val inflater = navHostFragment.navController.navInflater
                val graph = inflater.inflate(R.navigation.nav_graph_drawer)
                graph.setStartDestination(R.id.trip)
                val navController = navHostFragment.navController
                val nameArg = NavArgument.Builder().setDefaultValue(Gson().toJson(data)).build()
                val driverArg = NavArgument.Builder().setDefaultValue(Gson().toJson(driver)).build()
                graph.addArgument(Config.REQ_DATA, nameArg)
                graph.addArgument(Config.DRIVER_DATA, driverArg)
                navController.graph = graph
            }
        }
    }

    override fun setFloatService(b: Boolean) {
        if (!b) {
            stopForegroundBubbles()
        } else initListeners()
    }

    private fun initListeners() {
        if (checkHasDrawOverlayPermissions()) {
            if (vm.session.getBoolean(SessionMaintainence.DriverStatus)) {
                workManager()
            } else {
                stopForegroundBubbles()
            }

        } else {
            checkDrawOverPermission()
        }
    }

    private fun stopForegroundBubbles() {
        // stopService(Intent(this, FloatingService::class.java))
        // FloatingService.stopForegroundBubble()
        vm.session.saveBoolean(SessionMaintainence.DriverStatus,false) /* assign driver status false to avoid starting service in Floating service onDestroy */
        Log.e("CheckService", "${FloatingService.isServiceStart}")
        if (FloatingService.isServiceStart) {
            val intentStop = Intent(this, FloatingService::class.java)
            intentStop.action = ACTION_STOP_FOREGROUND
            startService(intentStop)
            stopService(Intent(this, FloatingService::class.java))
        }
    }


    private fun checkHasDrawOverlayPermissions(): Boolean {
        return Settings.canDrawOverlays(this)
    }


    override fun openLoginPage() {
        startActivity(Intent(this, SignupActivity::class.java))
    }

    override fun openMapFragment() {
        navController.currentDestination?.label?.let { label ->
            if (label != "Home" && label != getString(R.string.label_upload_document) && label != getString(R.string.text_profile))
                navController.navigate(R.id.map_fragment)
        }
    }

    override fun openApproveDeclinePage(
        approveStatus: Int?,
        documentStatus: Int?,
        blockReason: String?,
        showSubscribe: Boolean
    ) {
        navController.currentDestination?.label?.let { label ->
            if (label != getString(R.string.label_upload_document)){
                val inflater = navHostFragment.navController.navInflater
                val graph = inflater.inflate(R.navigation.nav_graph_drawer)
                graph.setStartDestination(R.id.approval_decline)
                val navController = navHostFragment.navController
                val aproveStatus = NavArgument.Builder().setDefaultValue("$approveStatus").build()
                val docsStatus = NavArgument.Builder().setDefaultValue("$documentStatus").build()
                val reason = NavArgument.Builder().setDefaultValue(blockReason ?: "").build()
                val showSub = NavArgument.Builder().setDefaultValue(showSubscribe).build()
                graph.addArgument(Config.APPROVE_STATUS, aproveStatus)
                graph.addArgument(Config.DOCS_STATUS, docsStatus)
                graph.addArgument(Config.BLOCK_REASON, reason)
                graph.addArgument(Config.SHOW_SUBSCRIBE, showSub)
                if (vm.reqInproData != null) {
                    val custNum =
                        NavArgument.Builder().setDefaultValue(vm.reqInproData?.customerCareNumber ?: "")
                            .build()
                    val headOfficeNum = NavArgument.Builder()
                        .setDefaultValue(vm.reqInproData?.headOfficeNumber?.value ?: "").build()
                    graph.addArgument(Config.CUSTOMER_CARE_NUM, custNum)
                    graph.addArgument(Config.HELPLINE_NUM, headOfficeNum)
                }
                navController.graph = graph
            }
        }
    }

    override fun callLogoutFromActivity() {
        vm.session.saveString(SessionMaintainence.AccessToken, "")
        startActivity(Intent(this, SignupActivity::class.java))
        finish()
    }

    override fun getCtx(): Context {
        return applicationContext
    }

    override fun getCurrentDest(): String {
        return navController.currentDestination?.label.toString()
    }

    val handler = Handler(Looper.getMainLooper())
    override fun checkForAdditionalPermissions() {
        handler.postDelayed(additionalPermissions, 1000)
    }

    override fun openInvoice(data: RequestResponseData) {
        val bundle = bundleOf("REQUEST_DATA" to data, "MODE" to 0)
        navController.navigate(R.id.trip_bill_frag, bundle)
    }

    private val logoutPushReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            vm.setToglleStatus.set(false)
        }

    }

    private val newRequestReceiver : BroadcastReceiver = object  : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            reqProgress()
        }
    }

    /**
     * Clear all the back fragments and opens the fresh one.
     */
    fun navigateFirstTabWithClearStack() {
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.nav_graph_drawer)
        graph.setStartDestination(R.id.map_fragment)
        val typeSlug = NavArgument.Builder().setDefaultValue(vm.typeSlug.get() ?: "").build()
        graph.addArgument(Config.vehicle_type_slug, typeSlug)
        val navController = navHostFragment.navController
        navController.graph = graph
        reqProgress()
    }

//    private fun startService() {
//        //if (checkHasDrawOverlayPermissions())
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
//            ContextCompat.startForegroundService(
//                this,
//                Intent(this, FloatingService::class.java)
//            )
//        else startService(Intent(this, FloatingService::class.java))
//    }

    fun workManager() {
        Log.e("CheckBubble", "Notification : ${checkIfNotification()}")
//        if (vm.session.getBoolean(SessionMaintainence.DriverStatus)) {
//            if (!vm.session.getBoolean(SessionMaintainence.ISSERVICESTARTED)) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
//                    ContextCompat.startForegroundService(
//                        this,
//                        Intent(this, FloatingService::class.java)
//                    )
//                else startService(Intent(this, FloatingService::class.java))
//                val periodicTask =
//                    PeriodicWorkRequest.Builder(MyWorkerClass::class.java, 15, TimeUnit.MINUTES)
//                        .setConstraints(Constraints.NONE)
//                        .build()
//                WorkManager.getInstance(this).enqueue(periodicTask)
//            } else {
//                if (MyApplication.notification == null) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
//                        ContextCompat.startForegroundService(
//                            this,
//                            Intent(this, FloatingService::class.java)
//                        )
//                    else startService(Intent(this, FloatingService::class.java))
//                    val periodicTask =
//                        PeriodicWorkRequest.Builder(MyWorkerClass::class.java, 15, TimeUnit.MINUTES)
//                            .setConstraints(Constraints.NONE)
//                            .build()
//                    WorkManager.getInstance(this).enqueue(periodicTask)
//                }
//            }
//        } else stopForegroundBubbles()


        if (vm.session.getBoolean(SessionMaintainence.DriverStatus)) {
            if (MyApplication.notification == null || !checkIfNotification()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    ContextCompat.startForegroundService(
                        this,
                        Intent(this, FloatingService::class.java)
                    )
                else startService(Intent(this, FloatingService::class.java))
                val periodicTask =
                    PeriodicWorkRequest.Builder(MyWorkerClass::class.java, 15, TimeUnit.MINUTES)
                        .setConstraints(Constraints.NONE)
                        .build()
                WorkManager.getInstance(this).enqueue(periodicTask)
            }
        } else stopForegroundBubbles()

    }

    fun checkIfNotification(): Boolean {
        val mNotificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notifications: Array<StatusBarNotification> = mNotificationManager.activeNotifications
        return notifications.isNotEmpty()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        vm.showProfilePic.set(false)
        if (id == R.id.nav_home) {
            reqProgress()
        } else if (id == R.id.nav_profile) {
            navController.navigate(R.id.profile_frag)
        } else if (id == R.id.nav_manage_doc) {
            navController.navigate(R.id.document_list)
        } else if (id == R.id.nav_support) {
            navController.navigate(R.id.support)
        } else if (id == R.id.nav_dashboard) {
            navController.navigate(R.id.dashboard)
        } else if (id == R.id.nav_history) {
            navController.navigate(R.id.history)
        } else if (id == R.id.nav_refferral) {
            navController.navigate(R.id.referral)
        } else if (id == R.id.nav_notification) {
            navController.navigate(R.id.notification)
        } else if (id == R.id.nav_wallet) {
            navController.navigate(R.id.wallet)
        } else if (id == R.id.nav_logout) {
            if (navController.currentDestination?.label?.contentEquals("Home", true) == true ||
                navController.currentDestination?.label?.contentEquals("approvalFrag", true) == true
            )
                showAlertDialog()
        } else if (id == R.id.nav_app_status)
            navController.navigate(R.id.app_status)
        else if(id == R.id.nav_language)
            navController.navigate(R.id.language)

//        else if (id == R.id.nav_subscription) {
//            if (navController.currentDestination?.label?.contentEquals("Home", true) == true)
//                navController.navigate(R.id.map_to_subscription)
//            else
//                navController.navigate(R.id.trip_to_subscription)
//        }
        vm.showProfilePic.set(false)
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true

    }

    fun closeDrawer() {
        if (mDrawer != null) {
            if (mDrawer!!.isOpen)
                mDrawer!!.close()
        }
    }

    override fun showTripCancelled() {
        val intent = Intent(Config.RECEIVE_CLOSE_TRIP)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    override fun updateTripAddress(intent: Intent) {
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
//        showTripAddressDialog(intent)
    }


    private fun showAlertDialog() {
        val logDialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        logDialog.setContentView(R.layout.custom_log_out_dialog)
        logDialog.show()


        val title = logDialog.findViewById<TextView>(R.id.log_title)
        val desc = logDialog.findViewById<TextView>(R.id.desc_log)
        val cancelButton = logDialog.findViewById<MaterialButton>(R.id.cancel_log)
        val logOUtButton = logDialog.findViewById<MaterialButton>(R.id.logOut_button)
        if (title != null) {
            title.text = vm.translationModel?.txt_logout
        }
        if (desc != null) {
            desc.text = vm.translationModel?.text_desc_logout
        }
        if (cancelButton != null) {
            cancelButton.text = vm.translationModel?.text_cancel
        }
        if (logOUtButton != null) {
            logOUtButton.text = vm.translationModel?.txt_logout
        }
        logOUtButton?.setOnClickListener {
            vm.getLogutApiVm()
            logDialog.dismiss()

        }
        cancelButton?.setOnClickListener {
            logDialog.dismiss()
        }
        logDialog.setCancelable(true)
        logDialog.show()
        logDialog.setOnKeyListener { arg0, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                logDialog.dismiss()
            }
            true
        }
    }


    private val additionalPermissions = Runnable {
        checkIsIgnoringBatteryOptimization()
        val deviceManufacturer = Build.MANUFACTURER
        if (!batteryOptimizationRemoved) {
            if (deviceManufacturer != Config.REAL_ME_DEVICE_NAME) {
                openOptimizingDialog()
            } else
            // autoStartPermission()
                checkDrawOverPermission()
        } else
            checkDrawOverPermission()
//            autoStartPermission()

    }


    override fun onPause() {
        super.onPause()
        if (vm.setToglleStatus.get()) {
            bubbleState = true
            val intent = Intent(Config.RECEIVE_BUBBLE_STATE)
            intent.putExtra(Config.BUBBLE_STATE, bubbleState)
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        }
        isDrawer = false
        MyApplication.drawerActpageState = false
        LocalBroadcastManager.getInstance(this).unregisterReceiver(refreshFromAppStatus)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
            locToRentPush
        )
    }

    fun autoStartPermission() {
        if (Build.MANUFACTURER.equals("oppo", ignoreCase = true)) {
            try {
                val intent = Intent()
                intent.setClassName(
                    "com.coloros.safecenter",
                    "com.coloros.safecenter.permission.startup.StartupAppListActivity"
                )
                startActivity(intent)
            } catch (e: Exception) {
                try {
                    val intent = Intent()
                    intent.setClassName(
                        "com.oppo.safe",
                        "com.oppo.safe.permission.startup.StartupAppListActivity"
                    )
                    startActivity(intent)
                } catch (ex: Exception) {
                    try {
                        val intent = Intent()
                        intent.setClassName(
                            "com.coloros.safecenter",
                            "com.coloros.safecenter.startupapp.StartupAppListActivity"
                        )
                        startActivity(intent)
                    } catch (exx: Exception) {
                    }
                }
            }
        } else if (Build.MANUFACTURER.equals("vivo")) {
            try {
                val intent = Intent()
                intent.component = ComponentName(
                    "com.iqoo.secure",
                    "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity"
                )
                startActivity(intent)
            } catch (e: Exception) {
                try {
                    val intent = Intent()
                    intent.component = ComponentName(
                        "com.vivo.permissionmanager",
                        "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
                    )
                    startActivity(intent)
                } catch (ex: Exception) {
                    try {
                        val intent = Intent()
                        intent.setClassName(
                            "com.iqoo.secure",
                            "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager"
                        )
                        startActivity(intent)
                    } catch (exx: Exception) {
                        ex.printStackTrace()
                    }
                }
            }
        }
    }

    var batteryOptimizationRemoved = false
    private fun checkIsIgnoringBatteryOptimization() {
        val packageName = packageName
        val pm = getSystemService(POWER_SERVICE) as PowerManager
        batteryOptimizationRemoved = pm.isIgnoringBatteryOptimizations(packageName)
    }


    private var resultBatteryOptimization =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

    private var optimizingDialog: Dialog? = null

    override fun getOptimizingDialog(): Dialog? {
        return optimizingDialog
    }

    private fun openOptimizingDialog() {
        if(!isFinishing){
            val prevFragment: Fragment? = supportFragmentManager.findFragmentByTag(
                OptimizingDialog.TAG
            )
            if (prevFragment != null) {
                return
            }
            val dialog = vm.translationModel?.let { OptimizingDialog.newInstance(it) }
                dialog?.show(supportFragmentManager, OptimizingDialog.TAG)
        }
    }


    private var resultDrawerOverPermission =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            checkDrawOverPermission()
        }

    var mAlert: BottomSheetDialog? = null
    override fun getmAlert(): BottomSheetDialog? {
        return mAlert
    }

    override fun currentLoc() {
        val intent = Intent(Config.RECEIVE_CURR_LOC_CLICK)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }


    private fun checkDrawOverPermission() {
        if (!Settings.canDrawOverlays(this)) {
            if ("xiaomi" == Build.MANUFACTURER.lowercase(Locale.ROOT)) {
                openExtraMiPermissions()
            } else {
                if (mAlert?.isShowing == true)
                    mAlert?.dismiss()
                mAlert = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
                mAlert?.setContentView(R.layout.draw_over_apps_dialog)
                val okButton = mAlert?.findViewById<Button>(R.id.ok_button_da)
                val title = mAlert?.findViewById<TextView>(R.id.title_da)
                val desc = mAlert?.findViewById<TextView>(R.id.desc_da)
                desc?.text = vm.translationModel?.txt_draw_apps_desc
                okButton?.setOnClickListener{
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")
                    )
                    resultDrawerOverPermission.launch(intent)
                    mAlert?.dismiss()
                }
                mAlert?.setCancelable(false)
                mAlert?.show()
            }
        } else {
            if (miPermissionDialog?.isShowing == true)
                miPermissionDialog?.dismiss()
            if (mAlert?.isShowing == true)
                mAlert?.dismiss()
        }
    }

    var miPermissionDialog: Dialog? = null
    override fun getMiPermission(): Dialog? {
        return miPermissionDialog
    }

    private fun openExtraMiPermissions() {
        if (miPermissionDialog?.isShowing == true)
            miPermissionDialog?.dismiss()
        miPermissionDialog = Dialog(this)
        miPermissionDialog?.setContentView(R.layout.dialog_mi_battery_optimization)
        miPermissionDialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        miPermissionDialog?.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        val gif = miPermissionDialog?.findViewById<ImageView>(R.id.imageOptimize)
        if (gif != null) {
            Glide.with(this).load(R.drawable.roda_raja_mi_permission).into(gif)
        }
        miPermissionDialog?.findViewById<Button>(R.id.logout_ok_button)?.setOnClickListener {
            val intent = Intent("miui.intent.action.APP_PERM_EDITOR")
            intent.setClassName(
                "com.miui.securitycenter",
                "com.miui.permcenter.permissions.PermissionsEditorActivity"
            )
            intent.putExtra("extra_pkgname", packageName)
            startActivity(intent)
            miPermissionDialog?.dismiss()
        }
        miPermissionDialog?.setCancelable(false)
        miPermissionDialog?.show()
    }


    private fun setUpNavMenuTranslation() {
        binding.navView.menu.findItem(R.id.nav_home).title = vm.translationModel?.txt_Home
        binding.navView.menu.findItem(R.id.nav_profile).title =
            (vm.translationModel?.txt_my_profile)
        binding.navView.menu.findItem(R.id.nav_manage_doc).title =
            vm.translationModel?.text_manage_documents
        binding.navView.menu.findItem(R.id.nav_support).title = vm.translationModel?.txt_support
        binding.navView.menu.findItem(R.id.nav_dashboard).title = vm.translationModel?.txt_dashboard
        binding.navView.menu.findItem(R.id.nav_history).title = vm.translationModel?.txt_my_rides
        binding.navView.menu.findItem(R.id.nav_refferral).title = vm.translationModel?.text_refferal
        binding.navView.menu.findItem(R.id.nav_notification).title =
            vm.translationModel?.txt_noitification
        binding.navView.menu.findItem(R.id.nav_wallet).title = vm.translationModel?.txt_wallet
        binding.navView.menu.findItem(R.id.nav_logout).title = vm.translationModel?.txt_logout
        binding.navView.menu.findItem(R.id.nav_app_status).title =
            vm.translationModel?.txt_app_status
        binding.navView.menu.findItem(R.id.nav_language).title = vm.translationModel?.txt_Lang

//        binding.navView.menu.findItem(R.id.nav_subscription).title =
//            vm.translationModel?.txt_subscription
    }

    private fun checkIfLocationEnabled(): Boolean {
        val lm = applicationContext.getSystemService(LOCATION_SERVICE) as LocationManager
        var gps_enabled = false
        var network_enabled = false
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: Exception) {
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: Exception) {
        }
        return (gps_enabled && network_enabled)
    }

    var enableLocation: Dialog? = null
    private fun showEnableLocation() {
        if (enableLocation == null || !enableLocation?.isShowing!!) {
            enableLocation = Dialog(this)
            enableLocation?.setContentView(R.layout.dialog_enable_location)
            enableLocation?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            enableLocation?.window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            enableLocation?.findViewById<TextView>(R.id.heading)!!.text =
                vm.translationModel?.txt_device_location_turned_off
            enableLocation?.findViewById<TextView>(R.id.locationDescription)!!.text =
                vm.translationModel?.txt_device_location_mandatory
            enableLocation?.findViewById<TextView>(R.id.allow)!!.text =
                vm.translationModel?.txt_allow
            enableLocation?.findViewById<CardView>(R.id.submit_butt)!!.setOnClickListener {
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                enableLocation?.dismiss()
            }
            enableLocation?.setCancelable(false)
            if (!(this as Activity).isFinishing) {
                enableLocation?.show()
            }
        }
    }

    override fun checkUpdate() {
        checkForAdditionalPermissions()

//        val appUpdateManager = AppUpdateManagerFactory.create(applicationContext)
//
//
//        // Returns an intent object that you use to check for an update.
//        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
//
//        // Checks that the platform will allow the specified type of update.
//        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
//            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
//                // This example applies an immediate update. To apply a flexible update
//                // instead, pass in AppUpdateType.FLEXIBLE
//                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
//            ) {
//                // Request the update.
//                requestUpdate(appUpdateInfo)
//            } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
//                requestUpdate(appUpdateInfo)
//            } else {
//                checkForAdditionalPermissions()
//                Log.d(TAG, "No Update available")
//            }
//        }
    }

    //to call reqin progroess if refresh called from app status
    private val refreshFromAppStatus: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            reqProgress()
        }
    }


    private fun requestUpdate(appUpdateInfo: AppUpdateInfo) {
        appUpdateManager?.startUpdateFlowForResult(
            // Pass the intent that is returned by 'getAppUpdateInfo()'.
            appUpdateInfo,
            // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
            AppUpdateType.IMMEDIATE,
            // The current activity making the update request.
            this,
            // Include a request code to later monitor this update request.
            Config.APP_UPDATE
        )

    }

    override fun onResume() {
        super.onResume()
        isDrawer = true
        LocalBroadcastManager.getInstance(this).registerReceiver(
            locToRentPush,
            IntentFilter(Config.LOC_TO_RENT_PUSH)
        )
        MyApplication.drawerActpageState = true
        appUpdateManager
            ?.appUpdateInfo
            ?.addOnSuccessListener { appUpdateInfo ->

                if (appUpdateInfo.updateAvailability()
                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                ) {
                    // If an in-app update is already running, resume the update.
                    appUpdateManager?.startUpdateFlowForResult(
                        appUpdateInfo,
                        IMMEDIATE,
                        this,
                        Config.APP_UPDATE
                    );
                }
            }

        if (!checkIfLocationEnabled()) {
            showEnableLocation()
        } else {
            if (enableLocation?.isShowing == true)
                enableLocation?.dismiss()
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(
            refreshFromAppStatus, IntentFilter(Config.CALL_REQIN_PRO_APPSTATUS)
        )
        reqProgress()
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            newRequestReceiver, IntentFilter(Config.NEW_REQUEST)
        )
        LocalBroadcastManager.getInstance(this).registerReceiver(
            logoutPushReceiver,
            IntentFilter(Config.LOGOUT_RECEIVER)
        )
        MyApplication.drawerActpageState = true
//        Intent(this, FloatingService::class.java).also { intent ->
//            bindService(intent, connection, Context.BIND_ADJUST_WITH_ACTIVITY)
//        }
        if (vm.setToglleStatus.get()) {
            bubbleState = false
            val intent = Intent(Config.RECEIVE_BUBBLE_STATE)
            intent.putExtra(Config.BUBBLE_STATE, bubbleState)
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        }

    }

    override fun onStop() {
        super.onStop()
        MyApplication.drawerActpageState = false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Config.APP_UPDATE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    Log.d(TAG, "" + "Result Ok")
                    //  handle user's approval }
                    reqProgress()
                }
                Activity.RESULT_CANCELED -> {
                    checkUpdate()
                    Log.d(TAG, "" + "Result Cancelled")
                    //  handle user's rejection  }
                }
                RESULT_IN_APP_UPDATE_FAILED -> {
                    checkUpdate()
                    Log.d(TAG, "" + "Update Failure")
                    //  handle update failure
                }
            }
        } else {
            if (vm.session.getBoolean(SessionMaintainence.DriverStatus)) {
                val periodicTask =
                    PeriodicWorkRequest.Builder(MyWorkerClass::class.java, 15, TimeUnit.MINUTES)
                        .setConstraints(Constraints.NONE)
                        .build()
                WorkManager.getInstance(this).enqueue(periodicTask)
            }
        }
    }

    private val locToRentPush: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            reqProgress()
        }
    }


//    override fun onBackPressed() {
//        if (mDrawer?.isOpen == true)
//            closeDrawer()
//        else
//            super.onBackPressed()
//    }




}