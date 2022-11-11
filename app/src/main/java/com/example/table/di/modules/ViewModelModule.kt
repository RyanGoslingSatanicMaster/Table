package com.example.table.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.table.components.fragments.GroupSelectionViewModel
import com.example.table.components.activity.MainViewModel
import com.example.table.components.activity.SplashScreenViewModel
import com.example.table.components.fragments.TimeTableViewModel
import com.example.table.annotations.ViewModelKey
import com.example.table.di.DaggerViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    abstract fun providesFactory(factory: DaggerViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(GroupSelectionViewModel::class)
    abstract fun providesGroupSelectionVM(viewModel: GroupSelectionViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun providesMainVM(viewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TimeTableViewModel::class)
    abstract fun providesTimeTableVM(viewModel: TimeTableViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SplashScreenViewModel::class)
    abstract fun providesSplashScreenVM(viewModel: SplashScreenViewModel): ViewModel

}