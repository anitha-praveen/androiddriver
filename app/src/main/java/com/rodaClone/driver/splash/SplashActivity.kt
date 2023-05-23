package com.rodaClone.driver.splash

import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.card.MaterialCardView
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.rodaClone.driver.BR
import com.rodaClone.driver.R
import com.rodaClone.driver.base.BaseAppActivity
import com.rodaClone.driver.connection.responseModels.AvailableCountryAndKLang
import com.rodaClone.driver.databinding.ActivitySplashBinding
import com.rodaClone.driver.drawer.DrawerActivity
import com.rodaClone.driver.loginSignup.SignupActivity
import com.rodaClone.driver.ut.Config
import com.rodaClone.driver.ut.SessionMaintainence
import com.rodaClone.driver.ut.Utilz
import javax.inject.Inject

class SplashActivity : BaseAppActivity<ActivitySplashBinding, SplashVM>(), SplashNavigator {

    companion object {
        const val TAG = "SplashActivity"
    }

    private lateinit var binding: ActivitySplashBinding
    lateinit var mainHandler: Handler
    private val permissions = Runnable { requestPermission() }

    private val normalLocationPermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (checkGranted(Config.Array_permissions)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    showDisclosureDialog()
                } else {
                    callNextActivity()
                }
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        ACCESS_FINE_LOCATION
                    )
                ) {
                    //Show permission explanation dialog...
                    requestPermission()

                } else {
                    //Never ask again selected, or device policy prohibits the app from having that permission.
                    //So, disable that feature, or fall back to another situation...
                    showLocationDenialDialog()
                }

            }

        }

    private var backgroundLocationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    ACCESS_BACKGROUND_LOCATION
                )
            ) {
                if (it)
                    callNextActivity()
                else {
                    isPermissionSettingsOpenedForAllowAllTime = true
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:$packageName")
                    )
                    intent.addCategory(Intent.CATEGORY_DEFAULT)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }
            } else {
                isPermissionSettingsOpenedForAllowAllTime = true
                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:$packageName")
                )
                intent.addCategory(Intent.CATEGORY_DEFAULT)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)

            }
        }


    @Inject
    lateinit var vmProviderFactory: ViewModelProvider.Factory
    val vm by lazy {
        ViewModelProvider(this, vmProviderFactory).get(SplashVM::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getmBinding()
        vm.setNavigator(this)
        if (!vm.session.getString(SessionMaintainence.CURRENT_LANGUAGE).isNullOrEmpty()) {
            vm.setLanguage(this, vm.session.getString(SessionMaintainence.CURRENT_LANGUAGE)!!)
        }else{
            vm.setLanguage(this,"en")
        }
        getTokenFromFirebase()
        vm.getLanguageCodeApi()

    }


    override fun getLayoutId() = R.layout.activity_splash

    override fun getBR() = BR.viewModel

    override fun getVMClass() = vm


    override fun startRequestingPermission() {
        mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post(permissions)
    }

    private fun requestPermission() {
        if (!checkGranted(Config.Array_permissions))
            normalLocationPermission.launch(Config.Array_permissions)
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && checkSelfPermission(
                    ACCESS_BACKGROUND_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                showDisclosureDialog()
            } else {
                callNextActivity()
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun showDisclosureDialog() {
        if (!isFinishing) {
            val builder =
                AlertDialog.Builder(this, R.style.RoundedDialogNarrow)
            builder.setCancelable(false)
            val view: View = layoutInflater.inflate(R.layout.layout_location_popup, null)
            builder.setView(view)
            val dialog = builder.create()
            dialog.show()
            val textView = dialog.findViewById<TextView>(R.id.txt_bg_location)
            val btnContinue = dialog.findViewById<Button>(R.id.btn_bg_continue)
            val btnExit = dialog.findViewById<Button>(R.id.btn_bg_exit)

            btnContinue!!.setOnClickListener { v: View? ->
                dialog.dismiss()
                backgroundLocationPermission.launch(ACCESS_BACKGROUND_LOCATION)
            }
            btnExit!!.setOnClickListener { v: View? -> finish() }
        }
    }

    override fun translationForCurrentLanguage(model: AvailableCountryAndKLang) {
        if (Utilz.isEmpty(vm.session.getString(SessionMaintainence.CURRENT_LANGUAGE)!!))
            startRequestingPermission()
        else {
            model.languages?.apply {
                forEach { languages ->
                    if (languages.code!! == vm.session.getString(SessionMaintainence.CURRENT_LANGUAGE)) {
                        vm.session.saveLong(
                            SessionMaintainence.TRANSLATION_TIME_OLD,
                            vm.session.getLong(SessionMaintainence.TRANSLATION_TIME_NOW)
                        )
                        vm.session.saveLong(
                            SessionMaintainence.TRANSLATION_TIME_NOW,
                            languages.updatedDate!!.toLong()
                        )
                        return@apply
                    }
                }
            }
            if ((vm.session.getLong(SessionMaintainence.TRANSLATION_TIME_OLD)) == 0L && (vm.session.getLong(
                    SessionMaintainence.TRANSLATION_TIME_NOW
                ) != 0L)
            ) {
                vm.session.saveLong(
                    SessionMaintainence.TRANSLATION_TIME_OLD,
                    vm.session.getLong(SessionMaintainence.TRANSLATION_TIME_NOW)!!
                )
            }
            if (vm.session.getLong(SessionMaintainence.TRANSLATION_TIME_NOW) != vm.session.getLong(
                    SessionMaintainence.TRANSLATION_TIME_OLD
                )
            ) {
                vm.taskId = 1
                vm.getSelectedLangBase(vm.session.getString(SessionMaintainence.CURRENT_LANGUAGE)!!)
            } else
                startRequestingPermission()
        }
    }

    var updateDialog: Dialog? = null
    override fun openAppUpdateDialog() {
        if (!isFinishing) {
            updateDialog = Dialog(this)
            if (updateDialog?.isShowing!!)
                updateDialog?.dismiss()
            updateDialog?.setContentView(R.layout.dialog_update_app)
            updateDialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            updateDialog?.window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            updateDialog?.findViewById<MaterialCardView>(R.id.continueBtn)?.setOnClickListener {
                try {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=$packageName")
                        )
                    )
                } catch (e: ActivityNotFoundException) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                        )
                    )
                }
                updateDialog?.dismiss()
            }
            updateDialog?.setCancelable(false)
            updateDialog?.show()
        }
    }

    private fun callNextActivity() {
        if (!Utilz.isEmpty(vm.session.getString(SessionMaintainence.AccessToken)!!)) {
            startActivity(Intent(this, DrawerActivity::class.java))
            finish()
        } else {
            startActivity(Intent(this, SignupActivity::class.java))
            finish()
        }
    }

    var isPermissionSettingsOpened = false
    var isPermissionSettingsOpenedForAllowAllTime = false
    override fun onResume() {
        super.onResume()
        if (isPermissionSettingsOpened && locationDenialDialog != null && !locationDenialDialog?.isShowing!! || isPermissionSettingsOpenedForAllowAllTime)
            requestPermission()
    }

    private fun getTokenFromFirebase() {
        FirebaseApp.initializeApp(this)
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
//                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            // Get new FCM registration token
            val token = task.result
            vm.session.saveString(SessionMaintainence.FCM_TOKEN, token.toString())
            Log.e("FcmKey",token.toString())
        })
    }

    var locationDenialDialog: Dialog? = null
    private fun showLocationDenialDialog() {
        locationDenialDialog = Dialog(this)
        if (locationDenialDialog?.isShowing!!)
            locationDenialDialog?.dismiss()
        locationDenialDialog?.setContentView(R.layout.dialog_location_permission_denied)
        locationDenialDialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        locationDenialDialog?.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        locationDenialDialog?.findViewById<TextView>(R.id.title)?.text =
            vm.translationModel?.txt_permission_denied ?: "Permission denied"
        locationDenialDialog?.findViewById<TextView>(R.id.description)?.text =
            vm.translationModel?.txt_permission_denied_desc
                ?: "Without location permission the app is unable to proceed further."
        locationDenialDialog?.findViewById<AppCompatButton>(R.id.allow)?.text =
            vm.translationModel?.txt_allow ?: "Allow"
        locationDenialDialog?.findViewById<AppCompatButton>(R.id.allow)?.setOnClickListener {
            locationDenialDialog?.dismiss()
            isPermissionSettingsOpened = true
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:$packageName")
            )
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)

        }
        locationDenialDialog?.setCancelable(false)
        locationDenialDialog?.show()
    }

}