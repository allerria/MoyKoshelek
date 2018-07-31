package ru.yandex.moykoshelek.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.yandex.moykoshelek.ui.main.MainActivity
import ru.yandex.moykoshelek.di.modules.MainModule

@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = [MainModule::class])
    abstract fun bindMainActivity (): MainActivity

}