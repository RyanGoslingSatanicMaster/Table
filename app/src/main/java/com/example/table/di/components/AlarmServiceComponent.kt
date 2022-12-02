package com.example.table.di.components

import com.example.table.components.services.AlarmService
import com.example.table.di.modules.ServiceModule
import dagger.Subcomponent

@Subcomponent(modules = [ServiceModule::class])
interface AlarmServiceComponent {
    fun inject(service: AlarmService)
}