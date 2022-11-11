package com.example.table.di.components

import com.example.table.components.activity.SplashScreenActivity
import com.example.table.annotations.PerActivity
import dagger.Subcomponent

@PerActivity
@Subcomponent
interface SplashScreenActivityComponent {
    fun inject(activity: SplashScreenActivity)
}