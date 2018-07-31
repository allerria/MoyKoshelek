package ru.yandex.moykoshelek.di

import dagger.Module
import ru.yandex.moykoshelek.di.modules.NavigationModule

@Module(includes = [NavigationModule::class])
class AppModule {

}