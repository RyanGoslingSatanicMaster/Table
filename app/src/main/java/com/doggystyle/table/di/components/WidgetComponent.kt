package com.doggystyle.table.di.components

import com.doggystyle.table.components.broadcasts.TimeTableWidgetReceiver
import com.doggystyle.table.di.modules.*
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [WidgetModule::class, RoomModule::class, GetDayWidgetModule::class])
interface WidgetComponent {
    fun inject(widget: TimeTableWidgetReceiver)
}
