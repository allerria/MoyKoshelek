package ru.yandex.moykoshelek.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.yandex.moykoshelek.main.MainActivity
import ru.yandex.moykoshelek.main.MainModule

@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = [MainModule::class])
    abstract fun bindMainActivity (): MainActivity

}