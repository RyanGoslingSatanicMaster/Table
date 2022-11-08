package com.example.table.di.components

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.table.MainActivity
import com.example.table.TableApp
import com.example.table.di.modules.*
import dagger.Component
import javax.inject.Provider
import javax.inject.Singleton
import kotlin.reflect.KClass

@Singleton
@Component(modules = [ApplicationModule::class, RoomModule::class, ViewModelModule::class, GroupModule::class, TimeTableModule::class])
interface ApplicationComponent {

    fun getMainActivityComponent(): MainActivityComponent

}