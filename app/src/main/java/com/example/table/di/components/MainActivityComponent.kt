package com.example.table.di.components

import com.example.table.MainActivity
import com.example.table.annotations.PerActivity
import com.example.table.di.modules.FragmentModule
import com.example.table.di.modules.GroupModule
import com.example.table.di.modules.TimeTableModule
import dagger.Subcomponent

@PerActivity
@Subcomponent(modules = [FragmentModule::class])
interface MainActivityComponent {
    fun inject(mainActivity: MainActivity)
}