package com.example.table.di.modules

import androidx.fragment.app.Fragment
import com.example.table.components.fragments.GroupSelectionFragment
import com.example.table.components.fragments.TimeTableFragment
import com.example.table.annotations.FragmentKey
import com.example.table.annotations.PerActivity
import com.example.table.components.fragments.SettingsFragment
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class FragmentModule {

    @Binds
    @IntoMap
    @PerActivity
    @FragmentKey(GroupSelectionFragment::class)
    abstract fun providesGroupSelectionFragment(fragment: GroupSelectionFragment): Fragment

    @Binds
    @IntoMap
    @PerActivity
    @FragmentKey(TimeTableFragment::class)
    abstract fun providesTimeTableFragment(fragment: TimeTableFragment): Fragment

    @Binds
    @IntoMap
    @PerActivity
    @FragmentKey(SettingsFragment::class)
    abstract fun providesSettingsFragment(fragment: SettingsFragment): Fragment
}