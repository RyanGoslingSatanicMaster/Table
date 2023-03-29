package com.doggystyle.table.di.components

import com.doggystyle.table.components.activity.SplashScreenActivity
import com.doggystyle.table.annotations.PerActivity
import dagger.Subcomponent

@PerActivity
@Subcomponent
interface SplashScreenActivityComponent {
    fun inject(activity: SplashScreenActivity)
}
