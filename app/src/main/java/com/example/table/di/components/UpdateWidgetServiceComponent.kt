package com.example.table.di.components

import com.example.table.components.services.AlarmService
import dagger.Subcomponent

@Subcomponent
interface UpdateWidgetServiceComponent {
    fun inject(service: AlarmService)
}