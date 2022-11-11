package com.example.table.di.components

import com.example.table.components.broadcasts.TimeTableWidgetReceiver
import com.example.table.di.modules.*
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [WidgetModule::class, RoomModule::class, GetDayWidgetModule::class])
interface WidgetComponent {
    fun inject(widget: TimeTableWidgetReceiver)
}