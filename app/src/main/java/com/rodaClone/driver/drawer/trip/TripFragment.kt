package com.rodaClone.driver.drawer.trip

import android.app.Activity
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Paint
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.ncorti.slidetoact.SlideToActView
import com.rodaClone.driver.BR
import com.rodaClone.driver.MyApplication
import com.rodaClone.driver.R
import com.rodaClone.driver.base.BaseFragment
import com.rodaClone.driver.connection.responseModels.RequestResponseData
import com.rodaClone.driver.databinding.TripFragLayBinding
import com.rodaClone.driver.dialogs.cancel.CancelDialog
import com.rodaClone.driver.dialogs.takeMeterPhoto.TakeMeterPhotoDialog
import com.rodaClone.driver.dialogs.tripOtp.TripOtpDialog
import com.rodaClone.driver.drawer.DrawerActivity
import com.rodaClone.driver.drawer.mapfragment.MapFragment
import com.rodaClone.driver.ut.Config
import com.rodaClone.driver.ut.SessionMaintainence
import com.rodaClone.driver.ut.Utilz
import java.io.File
import java.util.concurrent.Executors
import javax.inject.Inject


class TripFragment : BaseFragment<TripFragLayBinding, TripFragmentVM>(),
    TripNavigator, OnMapReadyCallback {
    companion object {
        const val TAG = "TripFragment"
    }

    lateinit var _binding: TripFragLayBinding
   // val args: TripFragmentArgs by navArgs()

    var mIntent: Intent? = null
    var alertMode = -1

    private var showChangeAllert = true
    private val otpDialog = TripOtpDialog()

    var permissionCamera = false
    var permissionReadExternalStorage = false
    var permissionWriteExternalStorage = false

    private var snapDialog: BottomSheetDialog? = null

    /*
    alertMode 0 -> Trip cancelled
              1 -> Address Change

    The alertMode variable is used to know whether which broadcast is received ( i.e cancel or address change)
    By knowing this we can check in resume wether trip change alert shown (in condition like when app is in background these broadcast cannot open the
    trip alert fragment. So we can open trip change alert with respective mode in on Resume. This alertMode value is reset to -1 inside method
     */
    private val closeTripPage: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            mIntent = intent
            alertMode = 0
            vm.removeWaitingTimeCallBacks()
            vm.mainHandler.removeCallbacks(vm.updateFRB)
            vm.isUpdatingDistanceAndTime = false

            vm.session.saveBoolean(SessionMaintainence.IS_GRACE_TIME_COMPLETED, false)
            vm.session.saveBoolean(SessionMaintainence.IS_GRACE_AFTER_START_COMPLETED, false)
            vm.session.saveInt(SessionMaintainence.SAVED_WAITING_TIME, 0)
            vm.session.saveInt(SessionMaintainence.SAVED_GRACE_TIME, 0)
            vm.session.saveInt(SessionMaintainence.SAVED_GRACE_TIME_AFTER_START, 0)
            vm.session.saveString(SessionMaintainence.DISPLAY_WAITING, "0.0")
            if (!DrawerActivity.isDrawer)
                tripAddressChangeOrCancelAlert(intent, true)
        }
    }


    private val startTripReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            vm.tripOtp = intent.getStringExtra(Config.otp)!!
            when {
                vm.isOutStationTrip.get() -> {
                    openMeterUploadDialog(0)
                }
                vm.isRentalTrip.get() -> {
                    openMeterUploadDialog(0)
                }
                else -> {
                    vm.startTrip()
                }
            }
        }

    }

    private val distanceReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            vm.tripDistance.set("${vm.round(intent.getDoubleExtra("DISTANCE", 0.0), 3)}")
        }
    }

    private val tripConvertion: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            vm.loadRequestData(
                intent.getStringExtra(Config.REQ_DATA), intent.getStringExtra(Config.DRIVER_DATA)
            )
        }
    }

    private val userNightPhoto: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.hasExtra(Config.retake) && intent.getBooleanExtra(Config.retake, false)) {
                showTakePhotoDialog(true)
                showSkipOrReject(false)
            } else {
                vm.isUserPicUpload.set(intent.getBooleanExtra(Config.upload_status, false))
                vm.userSelfie.set(intent.getStringExtra(Config.images))
            }
        }
    }

    private val pushNightPhoto: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            if (!vm.isDriverPicUpload) {
                if (vm.isInstantJob.get() || vm.isDispatch.get()) {
                    if (userDriverImg?.isShowing == false || userDriverImg == null)
                        uploadNightImageBoth()
                } else {
                    if (snapDialog?.isShowing == false || snapDialog == null)
                        showTakePhotoDialog(false)
                }
            }
        }

    }

    private val tripImageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (!vm.isTripStarted.get()) {
                vm.stratKM.set(intent.getStringExtra(Config.start_km)!!)
                vm.tripStartImagePath = intent.getStringExtra(Config.trip_image)!!
                vm.tripStartImage = File(vm.tripStartImagePath ?: "")
                vm.startTrip()
            } else {
                vm.endKM = intent.getStringExtra(Config.start_km)!!
                vm.tripEndImagePath = intent.getStringExtra(Config.trip_image)!!
                vm.tripEndImage = File(vm.tripEndImagePath ?: "")
                vm.callEndTripApi()
            }
        }

    }

    private val tripAddressChange: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            mIntent = intent
            alertMode = 1
            if (intent.getBooleanExtra(Config.isPickup, false)) {
                if (intent.getStringExtra(Config.pick_lat)!!
                        .isNotEmpty() && intent.getStringExtra(
                        Config.pick_lng
                    )!!.isNotEmpty()
                ) {
                    vm.pickupLatlng.set(
                        LatLng(
                            intent.getStringExtra(Config.pick_lat)!!.toDouble(),
                            intent.getStringExtra(Config.pick_lng)!!.toDouble()
                        )
                    )
                    vm.googleMap?.clear()
                    vm.polyLineDest1 = null
                    vm.polyLineDestDark = null
                    vm.pickupMarker = null
                    vm.dropMarker = null
//                    vm.drawPathPickToDrop(vm.pickupLatlng.get()!!, vm.dropLatlng.get()!!)
                }
                if (!DrawerActivity.isDrawer)
                    tripAddressChangeOrCancelAlert(intent, false)
            } else {
                if (intent.getStringExtra(Config.drop_lat)!!
                        .isNotEmpty() && intent.getStringExtra(
                        Config.drop_lng
                    )!!.isNotEmpty()
                ) {
                    vm.changdeDropLatlng.set(
                        LatLng(
                            intent.getStringExtra(Config.drop_lat)!!.toDouble(),
                            intent.getStringExtra(Config.drop_lng)!!.toDouble()
                        )
                    )
                    vm.googleMap?.clear()
                    vm.polyLineDest1 = null
                    vm.polyLineDestDark = null
                    vm.pickupMarker = null
                    vm.dropMarker = null
                    openAddressChangeAlert(
                        intent.getStringExtra(Config.drop_address)!!,
                        intent.getStringExtra(Config.loc_id)!!,
                        vm.changdeDropLatlng.get()!!
                    )
                }
            }
//
        }
    }

    private fun openAddressChangeAlert(stringExtra: String, loc_id: String, latLng: LatLng) {
//        alertMode = 1
        val dialog = context?.let { BottomSheetDialog(it, R.style.AppBottomSheetDialogTheme) }
        dialog?.setCancelable(false)
        dialog?.setContentView(R.layout.location_chage_confirm_lay)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)


        // val subHeading = dialog.findViewById<TextView>(R.id.desc_txt)
        val changedAddress = dialog?.findViewById<TextView>(R.id.changed_address)
        changedAddress?.setText(stringExtra)
        val yes: AppCompatButton? = dialog?.findViewById(R.id.yesButton)
        yes?.text = vm.translationModel?.text_yes

        val no: AppCompatButton? = dialog?.findViewById(R.id.noButton)
        no?.text = vm.translationModel?.text_no

        yes?.setOnClickListener {
            dialog.dismiss()
            vm.getPolyStringAndAcceptDropChange(
                vm.currentLatlng.get() ?: LatLng(
                    vm.session.getString(SessionMaintainence.CURRENT_LATITUDE)!!.toDouble(),
                    vm.session.getString(SessionMaintainence.CURRENT_LONGITUDE)!!.toDouble()
                ), latLng, stringExtra, loc_id, 1
            )
            stopSound()
        }
        no?.setOnClickListener {
            dialog.dismiss()
            vm.approveLocation(stringExtra, loc_id, 0, "")
            stopSound()
        }
        if (showChangeAllert) {
            dialog?.context?.let { playSong(it) }
            dialog?.show()
            showChangeAllert = false
        }
        dialog?.setOnDismissListener {
            showChangeAllert = true
        }
    }

    var mMediaPlayer: MediaPlayer? = null


    fun playSong(context: Context) {
        vm.printE("insidePlay---", "__")
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND)
        mMediaPlayer = MediaPlayer.create(activity, R.raw.drop_change)
        mMediaPlayer?.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        )

        //  if (mMediaPlayer != null) {
        if ((mMediaPlayer?.isPlaying) == false) {
            mMediaPlayer?.start()
            mMediaPlayer?.isLooping = true
        }
        //  }
    }

    fun stopSound() {
        if (mMediaPlayer != null) {
            mMediaPlayer!!.stop()
            mMediaPlayer!!.release()
            mMediaPlayer = null
        }
    }


    private val myExecutor = Executors.newSingleThreadExecutor()
    private val myHandler = Handler(Looper.getMainLooper())


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    val vm by lazy {
        ViewModelProvider(this, viewModelFactory).get(TripFragmentVM::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerBackPress()
        registerLocalBroadCastReceivers()
    }

    override fun onStart() {
        super.onStart()
        TripObject.insideTripFrag = true
    }

    override fun onStop() {
        super.onStop()
        TripObject.insideTripFrag = false
    }

    override fun onDestroy() {
        super.onDestroy()
        vm.removeLocUpdate()
        vm.removeWaitingTimeCallBacks()
        vm.mainHandler.removeCallbacks(vm.updateFRB)
        vm.isUpdatingDistanceAndTime = false
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(closeTripPage)
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(tripAddressChange)
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(startTripReceiver)
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(tripImageReceiver)
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(distanceReceiver)
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(tripConvertion)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = getmBinding()
        vm.setNavigator(this)

        if (vm.session.getString(SessionMaintainence.ReqID)?.trim()!!.isNotEmpty())
            setOngoingTripValues()
        initializeMapAndLocationUpdate()
        setCancelSlideListener()

        val sheetBehavior: BottomSheetBehavior<*> =
            BottomSheetBehavior.from<View>(_binding.tripBottomSheet)
        sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        _binding.tripBottomSheet.setOnClickListener { v ->
            if (sheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
            } else {
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
            }
        }
        if (isAdded)
            snapDialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        else
            snapDialog = BottomSheetDialog(
                requireActivity().applicationContext,
                R.style.AppBottomSheetDialogTheme
            )

        showSkipOrReject(false)
        _binding.skipTxt.paintFlags = _binding.skipTxt.paintFlags or Paint.UNDERLINE_TEXT_FLAG
    }

    override fun onMapReady(googleMap: GoogleMap) {
        vm.googleMap = googleMap
        setInitialLocation()
        vm.changeMapStyle(googleMap, requireContext())
    }

    private fun setInitialLocation() {
        vm.getLastLocation(requireContext())
        vm.loadRequestData(
            requireArguments().getString(Config.REQ_DATA),
            requireArguments().getString(Config.DRIVER_DATA)
        )
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            distanceReceiver,
            IntentFilter(Config.RECEIVE_DISTANCE)
        )
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            tripConvertion,
            IntentFilter(Config.RECEIVE_TRIP_DETAILS)
        )
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            userNightPhoto,
            IntentFilter(Config.USER_NIGHT_PHOTO)
        )
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            pushNightPhoto,
            IntentFilter(Config.PUSH_UPLOAD_NIGHT)
        )
        if (vm.session.getString(SessionMaintainence.SAVED_DISTANCE_TRAVELLED)?.isNotEmpty()!!) {
            vm.tripDistance.set(
                "${
                    vm.round(
                        vm.session.getString(SessionMaintainence.SAVED_DISTANCE_TRAVELLED)!!
                            .toDouble(), 3
                    )
                }"
            )
        }
        vm.requestLocationUpdates(requireActivity())
        if (alertMode == 0 && mIntent != null)
            tripAddressChangeOrCancelAlert(mIntent!!, true)
//        else if (alertMode == 1 && mIntent != null)
//            tripAddressChangeOrCancelAlert(mIntent!!, false)
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(userNightPhoto)
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(pushNightPhoto)
    }


    override fun getLayoutId() = R.layout.trip_frag_lay

    override fun getBR() = BR.viewModel

    override fun getVMClass() = vm

    private var backPressed = false
    fun closeFragment() {
        if (backPressed)
            requireActivity().finishAffinity()
        else
            showMessage(vm.translationModel?.txt_press_back_again ?: "")
        backPressed = true
        Handler(Looper.getMainLooper()).postDelayed(
            { backPressed = false },
            2000
        )
    }

    override fun getCtx(): Context? {
        return if (isAdded)
            requireContext()
        else null
    }

    override fun getMarkerIcon(icPickPin: Int): BitmapDescriptor? {
        return Utilz.getBitmapFromDrawable(
            requireContext(),
            icPickPin
        )?.let {
            BitmapDescriptorFactory.fromBitmap(
                it
            )
        }

    }

    override fun loadBill(data: RequestResponseData) {
        requireActivity().runOnUiThread {
            if (isAdded && findNavController().currentDestination?.label == "Trip Frag") {
                val bundle = bundleOf("REQUEST_DATA" to data, "MODE" to 0)
                findNavController().navigate(R.id.trip_to_tripbill, bundle)
                parentFragmentManager.beginTransaction().remove(this@TripFragment)
                    .commitAllowingStateLoss()
            }
        }

    }

    override fun openOtpDialog() {
        if (!otpDialog.isAdded) {
            otpDialog.show(childFragmentManager, TripOtpDialog.TAG)
        }
    }

    override fun openApproveAlert() {
        openAddressChangeAlert(
            vm.changedDropAddress.get()!!,
            vm.changedLocId.get()!!,
            vm.changdeDropLatlng.get()!!
        )
    }

    override fun loadRequestProgress() {
        (mActivity as DrawerActivity).accessDrawerAct()
    }

    override fun openSideMenu() {
        (mActivity as DrawerActivity).openSideMenu()
    }

    override fun openSos() {
        findNavController().navigate(R.id.trip_to_sos)
    }

    override fun loadProfile(toString: String) {
        if (isAdded)
            Glide.with(requireActivity()).load(toString)
                .apply(
                    RequestOptions().error(R.drawable.profile_place_holder)
                        .placeholder(R.drawable.profile_place_holder)
                )
                .into(_binding.imgUserPic)
    }

    override fun openMeterUploadDialog(mode: Int) {
        var totalKms: Int
        try {
            totalKms = vm.stratKM.get()?.let { Integer.parseInt(it) }!!;
        } catch (ex: NumberFormatException) {
            totalKms = 0
        }

        val meterPhotoDialog = TakeMeterPhotoDialog(mode, totalKms)
        meterPhotoDialog.show(
            childFragmentManager,
            TakeMeterPhotoDialog.TAG
        )
    }

    var endTripDialog: BottomSheetDialog? = null
    override fun showConfirmEndTripAlert() {
        if (endTripDialog?.isShowing == true)
            endTripDialog?.dismiss()
        endTripDialog =
            context?.let { BottomSheetDialog(it, R.style.AppBottomSheetDialogTheme) }
        endTripDialog?.setContentView(R.layout.end_trip_alert)
        endTripDialog?.show()
        val title = endTripDialog?.findViewById<TextView>(R.id.end_title)
        val desc = endTripDialog?.findViewById<TextView>(R.id.end_descrtiption)
        val cancelButton = endTripDialog?.findViewById<AppCompatButton>(R.id.cancel_end)
        val okButton = endTripDialog?.findViewById<AppCompatButton>(R.id.end_button)

        title?.text = vm.translationModel?.txt_end_trip
        desc?.text = vm.translationModel?.txt_end_trip_description
        okButton?.text = vm.translationModel?.text_end_trip
        cancelButton?.text = vm.translationModel?.text_cancel

        cancelButton?.setOnClickListener {
            endTripDialog?.dismiss()
        }
        okButton?.setOnClickListener {
            endTripDialog?.dismiss()
            vm.getAddressAndEndTrip()
        }
        endTripDialog?.setCanceledOnTouchOutside(false)

    }

    var resultLauncherCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                vm.onCaptureImageResult(data!!)
            }
        }

    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {

                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                    putExtra("com.google.assistant.extra.USE_FRONT_CAMERA", true)
                    putExtra("android.intent.extra.USE_FRONT_CAMERA", true)
                    putExtra("android.intent.extras.LENS_FACING_FRONT", 1)
                    putExtra("android.intent.extras.CAMERA_FACING", 1)

                    // Samsung
                    putExtra("camerafacing", "front")
                    putExtra("previous_mode", "front")

                    // Huawei
                    putExtra("default_camera", "1")
                    putExtra("default_mode", "com.huawei.camera2.mode.photo.PhotoMode")
                }
                resultLauncherCamera.launch(intent)

                // Permission is granted. Continue the action or workflow in your
                // app.
            } else {
                // Explain to the user that the feature is unavailable because the
                // features requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
            }
        }

    private fun openCamera() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        } else {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra("com.google.assistant.extra.USE_FRONT_CAMERA", true)
            intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true)
            intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1)
            intent.putExtra("android.intent.extras.CAMERA_FACING", 1)

            // Samsung
            intent.putExtra("camerafacing", "front")
            intent.putExtra("previous_mode", "front")

            // Huawei
            intent.putExtra("default_camera", "1")
            intent.putExtra("default_mode", "com.huawei.camera2.mode.photo.PhotoMode")
            resultLauncherCamera.launch(intent)
        }
    }

    override fun showTakePhotoDialog(isRetake: Boolean) {
        snapDialog?.setContentView(R.layout.take_photo_dialog)
        if (snapDialog?.isShowing == true) {
            snapDialog?.dismiss()
            snapDialog?.show()
        } else {
            snapDialog?.show()
        }
        snapDialog?.setCancelable(false)
        val title = snapDialog?.findViewById<TextView>(R.id.snap_title)
        val desc = snapDialog?.findViewById<TextView>(R.id.desc_snap)
        val proceedButton = snapDialog?.findViewById<MaterialButton>(R.id.proceed_button)
        title?.text = vm.translationModel?.txt_snap_title
        if (isRetake)
            desc?.text = vm.translationModel?.txt_retake_driver_desc
        else
            desc?.text = vm.translationModel?.txt_snap_description
        proceedButton?.text = vm.translationModel?.txt_proceed
        proceedButton?.setOnClickListener {
            snapDialog?.dismiss()
            /*
       camera flag
        1-> for driver image
        2-> for driverImage for dispatcher or instant trip
        3-> for userImage for dispatcher or instant trip
       */
            vm.cameraFlag = 1
            openCamera()
        }

    }

    override fun showSkipOrReject(value: Boolean) {
        val tSheetBehavior: BottomSheetBehavior<*> =
            BottomSheetBehavior.from<View>(_binding.tripBottomSheet)
        if (value) {
            tSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        } else
            tSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        val sheetBehavior: BottomSheetBehavior<*> =
            BottomSheetBehavior.from<View>(_binding.skipOrReject)
        sheetBehavior.state =
            (if (value) BottomSheetBehavior.STATE_COLLAPSED else BottomSheetBehavior.STATE_HIDDEN)
    }

    override fun openCancelReasons() {
        val fm = requireActivity().supportFragmentManager
        val dialog = CancelDialog().apply {
            arguments = Bundle().apply {
                putString(Config.requestData, Gson().toJson(vm.requestData))
                putBoolean(Config.isArrivedStatus, vm.isArrived.get())
            }
        }
        dialog.show(fm, CancelDialog.TAG)

    }

    private var userDriverImg: BottomSheetDialog? = null
    override fun uploadNightImageBoth() {
//        userDriverImg = if (isAdded)
//            BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
//        else
//            BottomSheetDialog(
//                MyApplication.appContext!!,
//                R.style.AppBottomSheetDialogTheme
//            )
//        userDriverImg?.setContentView(R.layout.dialog_user_driver_image)
//        if (userDriverImg?.isShowing == true) {
//            userDriverImg?.dismiss()
//            userDriverImg?.show()
//        } else {
//            userDriverImg?.show()
//        }
//        userDriverImg?.setCancelable(false)
//        val title = userDriverImg?.findViewById<TextView>(R.id.snap_title)
//        val desc = userDriverImg?.findViewById<TextView>(R.id.desc_snap)
//        val submit = userDriverImg?.findViewById<MaterialButton>(R.id.submit)
//        val user = userDriverImg?.findViewById<ImageView>(R.id.userImg)
//        val driver = userDriverImg?.findViewById<ImageView>(R.id.driverImg)
//        val userTxt = userDriverImg?.findViewById<TextView>(R.id.txt_user)
//        val driverTxt = userDriverImg?.findViewById<TextView>(R.id.txt_driver)
//        title?.text = vm.translationModel?.txt_snap_title
//        desc?.text = vm.translationModel?.txt_both_image_snap_desc
//        submit?.text = vm.translationModel?.text_submit
//        userTxt?.text = vm.translationModel?.txt_customer
//        driverTxt?.text = vm.translationModel?.txt_driver
//        /*
//        camera flag
//         1-> for driver image
//         2-> for driverImage for dispatcher or instant trip
//         3-> for userImage for dispatcher or instant trip
//        */
//        user?.setOnClickListener {
//            vm.cameraFlag = 3
//            if (vm.userPath != null)
//                showImage(vm.translationModel?.txt_customer ?: "", vm.userPath!!, true)
//            else
//                openCamera()
//        }
//
//        driver?.setOnClickListener {
//            vm.cameraFlag = 2
//            if (vm.driverPath != null)
//                showImage(vm.translationModel?.txt_driver ?: "", vm.driverPath!!, true)
//            else
//                openCamera()
//
//        }
//
//        submit?.setOnClickListener {
//            if (vm.driverPath == null) {
//                showMessage(vm.translationModel?.txt_upload_your_image ?: "")
//            } else {
//                if (vm.userPath == null)
//                    vm.uploadDriver()
//                else
//                    vm.uploadUserDriver()
//            }
//        }
    }

    override fun setUser(path: String) {
//        userDriverImg?.findViewById<ImageView>(R.id.userImg)?.let {
//            context?.let { it1 ->
//                Glide.with(it1).load(path).apply(
//                    RequestOptions().error(R.drawable.ic_user)
//                        .placeholder(R.drawable.ic_user).circleCrop()
//                ).into(it)
//            }
//        }
    }

    override fun setDriver(path: String) {
        userDriverImg?.findViewById<ImageView>(R.id.driverImg)?.let {
            context?.let { it1 ->
                Glide.with(it1).load(path).apply(
                    RequestOptions().error(R.drawable.profile_place_holder)
                        .placeholder(R.drawable.profile_place_holder)
                ).into(it)
            }
        }
    }

    override fun closeUserDriverDialog() {
        userDriverImg?.dismiss()
    }

    private fun DrawerActivity.accessDrawerAct() {
        reqProgress()
    }


    fun tripAddressChangeOrCancelAlert(intent: Intent, isCancel: Boolean) {
        alertMode = -1
        if (isCancel) {
            if (findNavController().currentDestination?.label == "Trip Frag") {
                val bundle = bundleOf(Config.mode to 1)
               //val action = TripFragmentDirections.tripToTripCancel(1)
                findNavController().navigate(R.id.trip_to_trip_cancel,bundle)
            }
        } else {

            val bundle = bundleOf(Config.mode to 1)
//            val action = TripFragmentDirections.tripToTripCancel(
//                if (intent.getBooleanExtra(Config.isPickup, false)) 2
//                else 3
//            )
            if (findNavController().currentDestination?.label?.equals("Trip Frag") == true)
                findNavController().navigate(R.id.trip_to_trip_cancel,bundle)
        }
    }

    private fun registerLocalBroadCastReceivers() {
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            closeTripPage, IntentFilter(Config.RECEIVE_CLOSE_TRIP)
        )
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            tripAddressChange, IntentFilter(Config.RECEIVE_TRIP_ADDRESS_CHANGE)
        )
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            startTripReceiver, IntentFilter(Config.RECEIVE_TRIP_OTP)
        )
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            tripImageReceiver, IntentFilter(Config.RECEIVE_TRIP_IMAGE)
        )
    }

    private fun registerBackPress() {
        activity?.onBackPressedDispatcher?.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    closeFragment()
                }
            })
    }

    private fun setCancelSlideListener() {
        _binding.slideToCancel.onSlideCompleteListener =
            (object : SlideToActView.OnSlideCompleteListener {
                override fun onSlideComplete(view: SlideToActView) {
                    openCancelReasons()
                    _binding.slideToCancel.resetSlider()
                }

            })

    }


    private fun initializeMapAndLocationUpdate() {
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.trip_map_frag) as? SupportMapFragment
        mapFragment!!.getMapAsync(this)
        vm.fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        vm.requestLocationUpdates(requireActivity())
    }

    private fun setOngoingTripValues() {
        vm.seconds = vm.session.getInt(SessionMaintainence.SAVED_WAITING_TIME)
        vm.graceArriveTime = vm.session.getInt(SessionMaintainence.SAVED_GRACE_TIME)
        vm.graceStartTime = vm.session.getInt(SessionMaintainence.SAVED_GRACE_TIME_AFTER_START)
        vm.waitingTime.set(vm.session.getString(SessionMaintainence.DISPLAY_WAITING))
    }

    var imageDialog: Dialog? = null
    override fun showImage(title: String, url: String, enableOption: Boolean) {
        if (imageDialog?.isShowing == true)
            imageDialog?.dismiss()
        imageDialog = Dialog(requireContext())
        imageDialog?.setContentView(R.layout.dialog_show_image)
        val profileTitle = imageDialog?.findViewById<TextView>(R.id.profile_txt)
        val camera = imageDialog?.findViewById<TextView>(R.id.camera)
        val profile = imageDialog?.findViewById<ImageView>(R.id.profile_image)
        camera?.text = vm.translationModel?.text_camera
        camera?.visibility = if (enableOption) View.VISIBLE else View.GONE
        profileTitle?.text = title
        if (isAdded) {
            context?.let {
                if (profile != null) {
                    Glide.with(it).load(url).apply(
                        RequestOptions.errorOf(R.drawable.ic_user)
                            .placeholder(R.drawable.ic_user)
                    ).into(profile)
                }
            }
        }
        camera?.setOnClickListener {
            openCamera()
            imageDialog?.dismiss()
        }
        imageDialog?.show()
    }
}