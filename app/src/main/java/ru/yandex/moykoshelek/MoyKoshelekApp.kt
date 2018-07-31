package ru.yandex.moykoshelek

import android.app.Application
import com.androidnetworking.AndroidNetworking
import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import ru.yandex.moykoshelek.di.DaggerAppComponent
import timber.log.Timber


class MoyKoshelekApp : DaggerApplication() {

    companion object {
        @get:Synchronized
        var instance: MoyKoshelekApp? = null
            private set
    }

    override fun onCreate() {
        super.onCreate()
        AndroidNetworking.initialize(applicationContext)
        InternetAvailabilityChecker.init(this)
        instance = this
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication>
            = DaggerAppComponent.builder().application(this).build()
}