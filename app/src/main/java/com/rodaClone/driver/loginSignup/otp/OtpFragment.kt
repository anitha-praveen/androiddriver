package com.rodaClone.driver.loginSignup.otp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.rodaClone.driver.BR
import com.rodaClone.driver.MyApplication
import com.rodaClone.driver.base.BaseFragment
import com.rodaClone.driver.databinding.FragmentOtpBinding
import com.rodaClone.driver.drawer.DrawerActivity
import com.rodaClone.driver.ut.MySMSBroadcastReceiver
import com.rodaClone.driver.ut.SessionMaintainence
import com.rodaClone.driver.R
import com.rodaClone.driver.ut.Config
import java.util.HashMap
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class OtpFragment : BaseFragment<FragmentOtpBinding, OtpVM>(),
    OtpNavigator {
    companion object {
        const val TAG = "OtpFrag"
    }

    //auto fill
    val task = context?.let { SmsRetriever.getClient(it).startSmsUserConsent(null) }
    private var intentFilter: IntentFilter? = null
    private var smsReceiver: MySMSBroadcastReceiver? = null

    var mAuth: FirebaseAuth? = null
    private lateinit var binding: FragmentOtpBinding

    var mCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null
    private var mVerificationId = ""
    private var mResendToken: PhoneAuthProvider.ForceResendingToken? = null
    private var otpView: OTPView? = null
    private var mDatabase: DatabaseReference? = null
    var phoneOtp = HashMap<String, String>()
    var allowBackNavigation = true

    @Inject
    lateinit var vmProviderFactory: ViewModelProvider.Factory
    private val vm by lazy {
        ViewModelProvider(this, vmProviderFactory).get(OtpVM::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (allowBackNavigation) {
                    findNavController().popBackStack()
                    parentFragmentManager.beginTransaction().remove(this@OtpFragment).commit()
                }
            }
        })
        initSmsRetriver()
        initBroadCast()

        requireContext().registerReceiver(smsReceiver, intentFilter)

    }

    override fun onResume() {
        super.onResume()
        requireContext().registerReceiver(smsReceiver, intentFilter)
    }


    override fun onPause() {
        super.onPause()
        vm.isOtpButtonEnabled.set(true)
        requireContext().unregisterReceiver(smsReceiver)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getmBinding()
        vm.setNavigator(this)
        setValues()
        otpView = binding.optCustomview
//        setUpFirebaseOTP()
        getTokenFromFirebase()
        vm.callotpApi()
        vm.displayText.set("${vm.translationModel?.txt_chk_sms} ${vm.translationModel?.txt_pin_sent} ${vm.countryCode.get()}${vm.phoneNumber.get()}")
        mDatabase = FirebaseDatabase.getInstance().reference
    }


    private fun setValues() {
        arguments?.let {
            vm.phoneNumber.set(it.getString(Config.phone_number))
            vm.countryCode.set(it.getString(Config.country_code))
            vm.countryCodeId.set(it.getString(Config.country_code_id))
        }
        vm.startTimer(120000)
    }


    override fun getLayoutId() = R.layout.fragment_otp

    override fun getBR() = BR.viewModel

    override fun getVMClass() = vm

    private fun setUpFirebaseOTP() {
        mAuth = FirebaseAuth.getInstance()
        mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                vm.isLoading.value = false
                allowBackNavigation = true

                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
//                Log.e("VerifiFailed===", "onVerificationFailed", e)
//                Log.e("VerifiFailed===", "onVerificationFailedDetails" + e.localizedMessage)

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                    e.message?.let { showMessage(it) }
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
//                    Toast.makeText(OTPActivity.this, getTranslatedString(R.string.txt_sms_excedd), Toast.LENGTH_SHORT).show();
                    e.message?.let { showMessage(it) }
                }

                // Show a message and update the UI
                // ...

            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                vm.isLoading.value = false
//                Log.d(tag, "onCodeSent:$verificationId")
                //                Toast.makeText(getApplicationContext(), "OTP sent", Toast.LENGTH_SHORT).show();

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId
                mResendToken = token
                vm.isLoading.value = false
            }
        }

        sendVerificationCode(vm.countryCode.get() + vm.phoneNumber.get())
        vm.isLoading.value = true
    }


    private fun sendVerificationCode(phoneNumber: String?) {
        vm.isLoading.value = true
        val options = PhoneAuthOptions.newBuilder(mAuth!!)
            .setPhoneNumber(phoneNumber!!) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity()) // Activity (for callback binding)
            .setCallbacks(mCallbacks!!) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(
                requireActivity()
            ) { task ->
//                if (task.isSuccessful) {
//                    vm.isLoading.value = true
//                    // Sign in success, update UI with the signed-in user's information
//                    Log.d(TAG, "signInWithCredential:success")
//                    getCtx()?.let {
//                        FirebaseApp.initializeApp(it)
//                        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
//                            if (!task.isSuccessful) {
//                                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
//                                return@OnCompleteListener
//                            }
//                            // Get new FCM registration token
//                            val token = task.result
//                            vm.session.saveString(SessionMaintainence.FCM_TOKEN, token.toString())
//                            // Log and toast
//                            vm.printE("RefreshTokenOLD", token.toString())
//                            vm.printE(TAG,"${credential.smsCode}")
//                            otpView?.let { v ->
//                                v.getOTP()?.let {
//                                    if(it.isNotEmpty()){
//                                        updateOtpInFirebase(it)
//                                    }else{
//                                        credential.smsCode?.let { smsCode ->
//                                            vm.savedOtp = smsCode
//                                            updateOtpInFirebase(smsCode)
//                                        }
//                                    }
//                                }
//                            }
////                        vm.loginCall(token.toString())
//                        })
//                    }
//
//                    // Update UI
//                } else {
//                    allowBackNavigation = true
//                    // Sign in failed, display a message and update the UI
//                    showSnackBar(task.exception!!.message!!)
//                    Log.w(tag, "signInWithCredential:failure", task.exception)
//                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
//
//                    }
//                }
            }
    }

    override fun verifyCode(otp: String?) {
        try {
            val credential = PhoneAuthProvider.getCredential(mVerificationId, otp!!)
            signInWithPhoneAuthCredential(credential)
        } catch (e: Exception) {
            vm.isLoading.value = false
            allowBackNavigation = true
            if (otp!!.isEmpty())
                Toast.makeText(requireContext(), "Enter OTP", Toast.LENGTH_SHORT).show()
            else Toast.makeText(requireContext(), "Wrong OTP", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getOpt(): String? {
        return if (otpView!!.getOTP()!!.isNotEmpty())
            otpView!!.getOTP()
        else
            vm.savedOtp
    }

    override fun goToSignUp() {
        // vm.printE(TAG,"SignUp")
        val bundle = bundleOf(Config.phone_number to  vm.phoneNumber.get()!!, Config.country_code to  vm.countryCode.get()!!,Config.country_code_id to vm.countryCodeId.get()!!)
        if (findNavController().currentDestination?.label == "Otp")
            findNavController().navigate(R.id.toSignup,bundle)
    }

    override fun goToDrawer() {
        getCtx()?.let {
            startActivity(Intent(it, DrawerActivity::class.java))
        }
    }

    override fun getAct(): Activity {
        return requireActivity()
    }

    override fun bacpressed() {
        closeFrag()
    }

    override fun getCtx(): Context? {
        return if (isAdded)
            requireContext()
        else
            MyApplication.appContext
    }

    override fun setAllowBackNav(value: Boolean) {
        allowBackNavigation = value
    }

    override fun callHideKeyBoard() {
        if (isAdded)
            hideKeyboard(requireActivity())
    }

    private fun updateOtpInFirebase(otp: String) {
        phoneOtp.clear()
        phoneOtp["otp"] = otp
        mDatabase!!.child("verification").child("driver")
            .child(vm.phoneNumber.get()!!).setValue(phoneOtp).addOnCompleteListener {
                // Write was successful!
                // ...
                if (vm.mode.get() == 0)
                    vm.loginOrSignUpApi()
                else
                    vm.updatePhoneInProfile()
            }

    }

    private fun closeFrag() {
        findNavController().popBackStack()
    }

    private fun getTokenFromFirebase() {
        FirebaseApp.initializeApp(requireContext())
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
//                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                vm.session.saveString(SessionMaintainence.FCM_TOKEN, "abcd")
                return@OnCompleteListener
            }
            // Get new FCM registration token
            val token = task.result
            vm.printE("FcmToken", token.toString())
            vm.session.saveString(SessionMaintainence.FCM_TOKEN, token.toString())
        })
    }

    private fun initSmsRetriver() {

        val client = context?.let { SmsRetriever.getClient(it /* context */) }
        val retiver = client?.startSmsRetriever()
        retiver?.addOnSuccessListener {
            vm.printE("SmsRetriver", "Started successfully")
        }?.addOnFailureListener {
            vm.printE("SmsRetriver", "Startd failed successfully")
        }

    }

    fun initBroadCast() {
        intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        smsReceiver = MySMSBroadcastReceiver()
        smsReceiver!!.setOTPListener(object : MySMSBroadcastReceiver.OTPReceiveListener {
            override fun onOTPReceived(otp: String?) {
                if (otpView!!.getOTP()!!.isNotEmpty())
                    otp?.let { otpView!!.setOTP(it) }

            }
        })

    }

}