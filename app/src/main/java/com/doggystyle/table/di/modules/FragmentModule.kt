package com.doggystyle.table.di.modules

import androidx.fragment.app.Fragment
import com.doggystyle.table.components.fragments.GroupSelectionFragment
import com.doggystyle.table.components.fragments.TimeTableFragment
import com.doggystyle.table.annotations.FragmentKey
import com.doggystyle.table.annotations.PerActivity
import com.doggystyle.table.components.fragments.SettingsFragment
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
