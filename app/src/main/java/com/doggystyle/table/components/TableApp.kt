package com.doggystyle.table.components

import android.app.Application
import android.content.Context
import com.doggystyle.table.di.components.ApplicationComponent
import com.doggystyle.table.di.components.DaggerApplicationComponent
import com.doggystyle.table.di.modules.ApplicationModule
import com.doggystyle.table.di.modules.RoomModule

class TableApp: Application() {

    lateinit var appComponent: ApplicationComponent

    @JvmName("getAppComponent1")
    fun getAppComponent(): ApplicationComponent{
        return com.doggystyle.table.di.components.DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(this))
            .roomModule(RoomModule(this))
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        appComponent = getAppComponent()
    }
}

// TODO Widget Feature
// TODO Add HomeTask in the App
// TODO Add NotificationService and Settings Feature
