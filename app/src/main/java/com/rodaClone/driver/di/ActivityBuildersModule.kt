package com.rodaClone.driver.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import com.rodaClone.driver.acceptReject.RespondRequest
import com.rodaClone.driver.drawer.DrawerActivity
import com.rodaClone.driver.loginSignup.SignupActivity
import com.rodaClone.driver.splash.SplashActivity
import com.rodaClone.driver.transparentAct.TranspActivity

@Module
abstract class ActivityBuildersModule {

    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    abstract fun contributeSplash(): SplashActivity

    @ContributesAndroidInjector(modules = [FragmentBuildersModule::class, ViewModelModule::class])
    abstract fun contributeDrawer(): DrawerActivity

    @ContributesAndroidInjector(modules = [FragmentBuildersModule::class, ViewModelModule::class])
    abstract fun contributeSignupActivity(): SignupActivity

    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    abstract fun contributeTranspActivity(): TranspActivity

}