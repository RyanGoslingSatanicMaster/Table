package com.doggystyle.table.components.broadcasts

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.doggystyle.table.components.activity.MainActivity
import com.doggystyle.table.components.worker.NotificationWorker

class AlarmReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == MainActivity.ALARM_ACTION || intent?.action == Intent.ACTION_BOOT_COMPLETED)
         context?.let {
             val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>().build()
             WorkManager.getInstance(it).enqueue(workRequest)
         }
    }
}
