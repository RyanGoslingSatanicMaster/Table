package com.doggystyle.table.di.modules

import android.app.Activity
import android.app.AlarmManager
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.doggystyle.table.components.activity.MainActivity
import com.doggystyle.table.utils.Constant
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
    fun provideAlarmManager() = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
}
