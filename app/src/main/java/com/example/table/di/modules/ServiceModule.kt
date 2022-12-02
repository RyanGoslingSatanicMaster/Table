package com.example.table.di.modules

import android.app.Activity
import android.app.AlarmManager
import android.app.Service
import android.content.Context
import com.example.table.components.services.AlarmService
import com.example.table.utils.Constant
import dagger.Module
import dagger.Provides

@Module
class ServiceModule constructor(private val service: AlarmService) {

    @Provides
    fun provideContext(): Context {
        return service
    }

    @Provides
    fun provideActivity(): Service {
        return service
    }

    @Provides
    fun provideSharedPref() = service.getSharedPreferences(Constant.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)

    @Provides
    fun provideAlarmManager() = service.getSystemService(Context.ALARM_SERVICE) as AlarmManager
}