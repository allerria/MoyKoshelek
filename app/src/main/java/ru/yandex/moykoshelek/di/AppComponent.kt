package ru.yandex.moykoshelek.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import ru.yandex.moykoshelek.MoyKoshelekApp
import ru.yandex.moykoshelek.di.modules.NavigationModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, AndroidSupportInjectionModule::class, AndroidInjectionModule::class, ActivityBuilder::class])
interface AppComponent: AndroidInjector<MoyKoshelekApp> {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): AppComponent.Builder

        fun build(): AppComponent
    }
}