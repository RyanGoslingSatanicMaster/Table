package com.example.table.di.components

import com.example.table.components.worker.NotificationWorker
import com.example.table.di.modules.*
import dagger.Component
import dagger.Subcomponent
import javax.inject.Singleton

@Singleton
@Component(modules = [RoomModule::class, GroupModule::class, TimeTableModule::class, WorkerModule::class])
interface NotificationWorkerComponent {
    fun inject(worker: NotificationWorker)
}
