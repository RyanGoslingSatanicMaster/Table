package com.example.table.di.modules

import android.app.Activity
import android.content.Context
import dagger.Module
import dagger.Provides

@Module
class ActivityModule constructor(private val activity: Activity) {

    @Provides
    fun provideContext(): Context{
        return activity
    }

    @Provides
    fun provideActivity(): Activity{
        return activity
    }
}