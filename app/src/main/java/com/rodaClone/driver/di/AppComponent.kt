package com.rodaClone.driver.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import com.rodaClone.driver.MyApplication
import javax.inject.Singleton

/**
 * @source https://medium.com/@shashankmohabia/taxiappz-android-with-com-dependency-injection-for-android-3a7e33ad1013
 * for Refference:
 * https://github.com/rohitpsoman/Android-Kotlin-MVVM-Navigation-Room-Coroutines-Databinding.git
 * */
@Singleton
@Component(
    modules = [AndroidSupportInjectionModule::class,
        ActivityBuildersModule::class,
        AppModule::class,
        ViewModelFactoryModule::class,
        NetworkModule::class]
)
interface AppComponent : AndroidInjector<MyApplication> {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): AppComponent
    }
}