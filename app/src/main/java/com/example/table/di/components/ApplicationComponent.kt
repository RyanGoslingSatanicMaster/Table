package com.example.table.di.components

import com.example.table.components.activity.MainActivity
import com.example.table.di.modules.*
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class, RoomModule::class, ViewModelModule::class, GroupModule::class, TimeTableModule::class])
interface ApplicationComponent {

    fun getMainActivityComponent(activityModule: ActivityModule): MainActivityComponent

    fun getSplashScreenActivityComponent(): SplashScreenActivityComponent

    fun getUpdateWidgetServiceComponent(): UpdateWidgetServiceComponent

}