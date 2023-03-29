package com.doggystyle.table.di.components

import com.doggystyle.table.components.activity.MainActivity
import com.doggystyle.table.annotations.PerActivity
import com.doggystyle.table.di.modules.ActivityModule
import com.doggystyle.table.di.modules.FragmentModule
import dagger.Subcomponent

@PerActivity
@Subcomponent(modules = [FragmentModule::class, ActivityModule::class])
interface MainActivityComponent {
    fun inject(mainActivity: MainActivity)
}
