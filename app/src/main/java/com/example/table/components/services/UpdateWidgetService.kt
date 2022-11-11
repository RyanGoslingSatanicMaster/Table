package com.example.table.components.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.LifecycleService
import com.example.table.components.TableApp
import com.example.table.di.components.UpdateWidgetServiceComponent
import com.example.table.usecases.GetActiveGroup
import com.example.table.usecases.GetTimeTableUseCase
import com.example.table.usecases.IGetActiveGroup
import com.example.table.usecases.IGetTimeTableUseCase
import javax.inject.Inject

class UpdateWidgetService : LifecycleService() {

    lateinit var serviceComponent: UpdateWidgetServiceComponent

    @Inject
    lateinit var getActiveGroup: IGetActiveGroup

    @Inject
    lateinit var getTimeTableUseCase: IGetTimeTableUseCase

    override fun onCreate() {
        super.onCreate()
        serviceComponent = (application as TableApp).appComponent.getUpdateWidgetServiceComponent()
        serviceComponent.inject(this)

    }

    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): UpdateWidgetService = this@UpdateWidgetService
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return binder
    }
}