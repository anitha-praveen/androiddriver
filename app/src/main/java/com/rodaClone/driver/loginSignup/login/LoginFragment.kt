package com.rodaClone.driver.loginSignup.login

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import com.rodaClone.driver.dialogs.notification_disclosure.NotificationPermissionDisclosure
import com.google.android.gms.location.LocationServices
import com.rodaClone.driver.BR
import com.rodaClone.driver.R
import com.rodaClone.driver.base.BaseFragment
import com.rodaClone.driver.connection.responseModels.AvailableCountryAndKLang
import com.rodaClone.driver.connection.responseModels.Country
import com.rodaClone.driver.databinding.FragmentLoginBinding
import com.rodaClone.driver.dialogs.countrylist.CountryListDialog
import com.rodaClone.driver.ut.Config
import com.rodaClone.driver.ut.SessionMaintainence
import com.rodaClone.driver.ut.Utilz
import javax.inject.Inject


class LoginFragment : BaseFragment<FragmentLoginBinding, LoginVM>(),
    LoginNavigator {
    companion object {
        const val TAG = "LoginFragment"
    }

    private var backPressed = false
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: FragmentLoginBinding
    lateinit var data: AvailableCountryAndKLang

    @Inject
    lateinit var vmProviderFactory: ViewModelProvider.Factory
    private val vm by lazy {
        ViewModelProvider(this, vmProviderFactory).get(LoginVM::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
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
        })

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getmBinding()
        vm.setNavigator(this)
        vm.fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        setupCountryList()
        binding.privacyTxt.text = vm.translationModel?.txt_terms_complete

        setClickableHighLightedText(
            binding.privacyTxt,
            vm.translationModel?.txt_privacy_policy ?: ""
        ) {
            // TODO: do your stuff here
        }
        setClickableHighLightedText(
            binding.privacyTxt,
            vm.translationModel?.txt_terms_of_use ?: ""
        ) {
            // TODO: do your stuff here
        }
        vm.requestLocationUpdates(requireActivity())
        vm.isTamilLanguage.set(vm.session.getString(SessionMaintainence.CURRENT_LANGUAGE) == "ta")
        //adjust resize soft keyboard enabled
//        binding.root.setOnApplyWindowInsetsListener{_,windowInsets ->
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                val imeHeight = windowInsets.getInsets(WindowInsets.Type.ime()).bottom
//                binding.root.setPadding(0, 0, 0, imeHeight)
//                val insets = windowInsets.getInsets(WindowInsets.Type.ime() or WindowInsets.Type.systemGestures())
//                insets
//            }
//
//            windowInsets
//        }

        //to generate hash string for sms retriver api
//        val stringGenerator = StringGenerator(context)
//        vm.printE("GeneratedHashString", stringGenerator.appSignatures[0])

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }
    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.e(TAG, "Notification permission granted")
            } else {
                if (isAdded) {
                    val prevFragment: Fragment? = childFragmentManager.findFragmentByTag(
                        NotificationPermissionDisclosure.TAG
                    )
                    if (prevFragment == null) {
                        val dialog =
                            NotificationPermissionDisclosure.newInstance(vm.translationModel)
                        dialog.show(childFragmentManager, NotificationPermissionDisclosure.TAG)
                    }
                }
            }
        }


    fun setClickableHighLightedText(
        tv: TextView,
        textToHighlight: String,
        onClickListener: View.OnClickListener?
    ) {
        val tvt = tv.text.toString()
        var ofe = tvt.indexOf(textToHighlight, 0)
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                onClickListener?.onClick(textView)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = ContextCompat.getColor(requireContext(), R.color.black)
                ds.typeface = ResourcesCompat.getFont(requireContext(), R.font.roboto_bold)
                ds.isUnderlineText = false
            }
        }
        val wordToSpan = SpannableString(tv.text)
        var ofs = 0
        while (ofs < tvt.length && ofe != -1) {
            ofe = tvt.indexOf(textToHighlight, ofs)
            if (ofe == -1) break else {
                wordToSpan.setSpan(
                    clickableSpan,
                    ofe,
                    ofe + textToHighlight.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                tv.setText(wordToSpan, TextView.BufferType.SPANNABLE)
                tv.movementMethod = LinkMovementMethod.getInstance()
            }
            ofs = ofe + 1
        }
    }


    override fun getLayoutId() = R.layout.fragment_login

    override fun getBR() = BR.viewModel

    override fun getVMClass() = vm

    override fun hideKeyBoard() {
        if (isAdded)
            Utilz.hideKeyboard(requireActivity())
    }

    override fun getContextAttached(): Context {
        return requireContext()
    }

    override fun openCountryListDialogue() {
        val newInstance = CountryListDialog(0)
        newInstance.show(requireActivity().supportFragmentManager, CountryListDialog.TAG)
    }

    override fun openOtp(phone: String, cc: String, ccId: String) {
        if (findNavController().currentDestination?.label == "Login") {
            val bundle = bundleOf(
                Config.phone_number to phone,
                Config.country_code to cc,
                Config.country_code_id to ccId
            )
            findNavController().navigate(R.id.toOtp, bundle)
        }
    }

//    override fun state(): Boolean {
//        return if (vm.isTamilLanguage.get()) binding.checkboxLoginT.isChecked
//        else binding.checkboxLogin.isChecked
//    }


    /*
    The below method setupCountryList will get the current location and check if the current location's
    country code matches with the country list obtained from splash . If matches then it will set it as
    default country else it will set default country as the first one from the country list
     */
    override fun setupCountryList() {
        data = vm.session.getAvailableCountryAndLanguages()!!
        data.country!!.forEach { country ->
            if (country.code!!.equals("FR", true) /*== vm.getCountryFromLatLng(
                    vm.currentLatlng.get()!!.latitude,
                    vm.currentLatlng.get()!!.longitude
                )*/
            ) {
                vm.countryModel = country
                return@forEach
            }
        }
        if (!vm.checkCountryModelIsInitialized())
            vm.countryModel = data.country!![0]

        vm.countryCode.set(vm.countryModel.dialCode)

    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            countryReceiver,
            IntentFilter(Config.RECEIVE_COUNTRY)
        )
    }

    private val countryReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            vm.countryModel = intent.getSerializableExtra(Config.country) as Country
            vm.countryCode.set(vm.countryModel.dialCode)
        }
    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(countryReceiver);
        super.onPause()
    }

}