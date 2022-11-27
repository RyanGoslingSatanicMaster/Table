package com.example.table.components.services

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.LifecycleService
import com.example.table.components.TableApp
import com.example.table.di.components.UpdateWidgetServiceComponent
import com.example.table.usecases.IGetActiveGroup
import com.example.table.usecases.IGetTimeTable
import javax.inject.Inject

class AlarmService : LifecycleService() {

    lateinit var serviceComponent: UpdateWidgetServiceComponent

    @Inject
    lateinit var getActiveGroup: IGetActiveGroup

    @Inject
    lateinit var getTimeTableUseCase: IGetTimeTable

    override fun onCreate() {
        super.onCreate()
        serviceComponent = (application as TableApp).appComponent.getUpdateWidgetServiceComponent()
        serviceComponent.inject(this)

    }

    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): AlarmService = this@AlarmService
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return binder
    }
}