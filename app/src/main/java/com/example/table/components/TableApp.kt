package com.example.table.components

import android.app.Application
import android.content.Context
import com.example.table.di.components.ApplicationComponent
import com.example.table.di.components.DaggerApplicationComponent
import com.example.table.di.modules.ApplicationModule
import com.example.table.di.modules.RoomModule

class TableApp: Application() {

    lateinit var appComponent: ApplicationComponent

    @JvmName("getAppComponent1")
    fun getAppComponent(): ApplicationComponent{
        return DaggerApplicationComponent.builder()
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