package com.example.table.components.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.SharedPreferences
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import com.example.table.R
import com.example.table.components.TableApp
import com.example.table.di.components.AlarmServiceComponent
import com.example.table.di.modules.ServiceModule
import com.example.table.usecases.IGetActiveGroup
import com.example.table.usecases.IGetTimeTable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class AlarmService : LifecycleService() {

    companion object{
        const val CHANNEL_ID = "ALARM_CHANNEL_TIMETABLE_APP"

    }

    lateinit var serviceComponent: AlarmServiceComponent

    @Inject
    lateinit var getActiveGroup: IGetActiveGroup

    @Inject
    lateinit var getTimeTableUseCase: IGetTimeTable

    @Inject
    lateinit var sharedPref: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        serviceComponent = (application as TableApp).appComponent.getAlarmServiceComponent(
            ServiceModule(this)
        )
        serviceComponent.inject(this)

        val remoteView = RemoteViews(this@AlarmService.packageName, R.layout.notification_item)
        val builder = NotificationCompat.Builder(this@AlarmService, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_search)
            .setContentTitle("Новое сообщение")
            .setContentText("Получено")
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
            .setAutoCancel(true)
            .setContent(remoteView)
        val notification = builder.build()
        notification.flags.or(Notification.FLAG_AUTO_CANCEL)
        val mNotificationManager =
            this@AlarmService.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(CHANNEL_ID,
                "TimeTableAppChannel",
                NotificationManager.IMPORTANCE_DEFAULT)
            mNotificationManager.createNotificationChannel(channel)
            startForeground(1, notification)
        }


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