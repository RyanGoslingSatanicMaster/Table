package com.doggystyle.table.di.components

import com.doggystyle.table.components.worker.NotificationWorker
import com.doggystyle.table.di.modules.*
import dagger.Component
import dagger.Subcomponent
import javax.inject.Singleton

@Singleton
@Component(modules = [RoomModule::class, GroupModule::class, TimeTableModule::class, WorkerModule::class])
interface NotificationWorkerComponent {
    fun inject(worker: NotificationWorker)
}
