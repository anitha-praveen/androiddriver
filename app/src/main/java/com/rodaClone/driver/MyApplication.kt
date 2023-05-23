package com.rodaClone.driver

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.app.Notification
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.bumptech.glide.request.target.ViewTarget
import com.google.android.gms.maps.MapsInitializer
import com.rodaClone.driver.acceptReject.RespondRequest
import com.rodaClone.driver.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication


/**
 * @see https://medium.com/@shashankmohabia/taxiappz-android-with-com-dependency-injection-for-android-3a7e33ad1013
 * */
class MyApplication : DaggerApplication(), LifecycleObserver,
    Application.ActivityLifecycleCallbacks {

    companion object {
        var appContext: Context? = null
        var inBackground = false
        var acceptRejectPageState = false
        var drawerActpageState = false

        @SuppressLint("StaticFieldLeak")
        var floatingControlView: ViewGroup? = null
        var notification: Notification? = null
    }






    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.factory().create(this)
    }

    override fun onCreate() {
        super.onCreate()
        ViewTarget.setTagId(R.id.glide_tag)
        appContext = this
        MapsInitializer.initialize(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

//        val componentName = ComponentName(
//            applicationContext,
//            MyFirebaseMessagingService::class.java
//        )
//        applicationContext.packageManager.setComponentEnabledSetting(
//            componentName,
//            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
//            PackageManager.DONT_KILL_APP
//        )
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onMoveToForeground() {
        // app moved to foreground
        inBackground = false
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onMoveToBackground() {
        // app moved to background
        inBackground = true
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

    }

    override fun onActivityStarted(activity: Activity) {

    }

    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {


    }
}