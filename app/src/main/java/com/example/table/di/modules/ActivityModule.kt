package com.example.table.di.modules

import android.app.Activity
import android.app.AlarmManager
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.table.components.activity.MainActivity
import dagger.Module
import dagger.Provides

@Module
class ActivityModule constructor(private val activity: MainActivity) {

    @Provides
    fun provideContext(): Context{
        return activity
    }

    @Provides
    fun provideActivity(): Activity{
        return activity
    }

    @Provides
    fun provideSharedPref() = activity.getPreferences(Context.MODE_PRIVATE)

    @Provides
    fun provideAlarmManager() = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
}