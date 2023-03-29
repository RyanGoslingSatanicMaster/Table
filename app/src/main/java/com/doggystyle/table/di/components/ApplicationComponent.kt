package com.doggystyle.table.di.components

import com.doggystyle.table.di.modules.*
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class, RoomModule::class, ViewModelModule::class, GroupModule::class, TimeTableModule::class])
interface ApplicationComponent {

    fun getMainActivityComponent(activityModule: ActivityModule): MainActivityComponent

    fun getSplashScreenActivityComponent(): SplashScreenActivityComponent

}
