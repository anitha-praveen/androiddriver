package com.rodaClone.driver.loginSignup.getStarteedScrn

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.LinearLayoutManager
import com.rodaClone.driver.BR
import com.rodaClone.driver.R
import com.rodaClone.driver.base.BaseFragment
import com.rodaClone.driver.connection.responseModels.AvailableCountryAndKLang
import com.rodaClone.driver.connection.responseModels.Languages
import com.rodaClone.driver.databinding.FragmentGetStartedBinding
import com.rodaClone.driver.loginSignup.SignupActivity
import com.rodaClone.driver.ut.SessionMaintainence
import javax.inject.Inject

class GetStartedScreen : BaseFragment<FragmentGetStartedBinding, GetStartedScreenVM>(),
    GetStartedScreenNavigator {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: FragmentGetStartedBinding
    lateinit var data: AvailableCountryAndKLang

    @Inject
    lateinit var vmProviderFactory: ViewModelProvider.Factory
    val vm by lazy {
        ViewModelProvider(this, vmProviderFactory).get(GetStartedScreenVM::class.java)
    }

    var backpressed = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (backpressed)
                    requireActivity().finishAffinity()
                else
                    showMessage("Press back again to exit")
                backpressed = true
                Handler(Looper.getMainLooper()).postDelayed(
                    { backpressed = false }, 2000
                )
            }
        })

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getmBinding()
        vm.setNavigator(this)
        setUpLanguageAdapter()
    }


    override fun getLayoutId() = R.layout.fragment_get_started

    override fun getBR() = BR.viewModel

    override fun getVMClass() = vm

    private fun setUpLanguageAdapter() {
        data = vm.session.getAvailableCountryAndLanguages()!!
        data.languages?.let {
            binding.langRecycler.layoutManager =
                LinearLayoutManager(
                    requireContext(), LinearLayoutManager.VERTICAL, false
                )
            val languageAdapter = LanguageAdapter(it, this)
            binding.langRecycler.adapter = languageAdapter
        }
    }

    override fun setSelectedLanguage(languages: Languages) {
        vm.selectedLangCode.set(languages.code!!)
        vm.session.saveString(SessionMaintainence.CURRENT_LANGUAGE, languages.code)
        vm.session.saveLong(
            SessionMaintainence.TRANSLATION_TIME_NOW,
            languages.updatedDate!!.toLong()
        )
    }

    override fun initiateNaviagation() {
        vm.session.saveBoolean(SessionMaintainence.GetStartedScrnLoaded, true)
     //  if(vm.session.getBoolean(SessionMaintainence.TOUR_SHOWN)){
            if(isAdded)
                (requireActivity() as SignupActivity).showLogin()
//        }else{
//            if(isAdded)
//                (requireActivity() as SignupActivity).showTour()
//        }
    }
}