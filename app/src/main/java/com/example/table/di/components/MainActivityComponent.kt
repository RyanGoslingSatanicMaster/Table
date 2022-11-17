package com.example.table.di.components

import com.example.table.components.activity.MainActivity
import com.example.table.annotations.PerActivity
import com.example.table.di.modules.ActivityModule
import com.example.table.di.modules.FragmentModule
import dagger.Subcomponent

@PerActivity
@Subcomponent(modules = [FragmentModule::class, ActivityModule::class])
interface MainActivityComponent {
    fun inject(mainActivity: MainActivity)
}