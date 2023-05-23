package com.rodaClone.driver.loginSignup

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import com.rodaClone.driver.BR
import com.rodaClone.driver.R
import com.rodaClone.driver.base.BaseAppActivity
import com.rodaClone.driver.databinding.ActivityLoginSignupBinding
import com.rodaClone.driver.ut.SessionMaintainence
import javax.inject.Inject

class SignupActivity : BaseAppActivity<ActivityLoginSignupBinding, SignupVM>(), SignupNavigator {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityLoginSignupBinding
    lateinit var navController: NavController

    @Inject
    lateinit var vmProviderFactory: ViewModelProvider.Factory
    val vm by lazy {
        ViewModelProvider(this, vmProviderFactory).get(SignupVM::class.java)
    }

    private lateinit var navHostFragment: NavHostFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getmBinding()
        vm.setNavigator(this)
        if (!vm.session.getString(SessionMaintainence.CURRENT_LANGUAGE).isNullOrEmpty()) {
            vm.setLanguage(this, vm.session.getString(SessionMaintainence.CURRENT_LANGUAGE)!!)
        }else{
            vm.setLanguage(this,"en")
        }

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navControlSignup) as NavHostFragment
        navController = navHostFragment.navController
        initiate()
    }

    override fun getLayoutId() = R.layout.activity_login_signup

    override fun getBR() = BR.viewModel

    override fun getVMClass() = vm

    private fun initiate() {
        if (!vm.session.getBoolean(SessionMaintainence.GetStartedScrnLoaded))
            showGetStarted()
//        else if (!vm.session.getBoolean(SessionMaintainence.TOUR_SHOWN))
//            showTour()
        else
            showLogin()
    }

    private fun showGetStarted() {
        navController.navigate(R.id.getStartedScreen)
    }

    fun showTour() {
        navController.navigate(R.id.tourGuide)
    }

    fun showLogin() {
        navController.navigate(R.id.login)
    }


}