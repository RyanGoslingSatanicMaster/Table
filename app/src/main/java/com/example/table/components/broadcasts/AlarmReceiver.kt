package com.example.table.components.broadcasts

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import com.example.table.components.activity.MainActivity
import com.example.table.components.services.AlarmService

class AlarmReceiver: BroadcastReceiver() {

    companion object{

    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.v("222", "333")
        if (intent?.action == MainActivity.ALARM_ACTION || intent?.action == Intent.ACTION_BOOT_COMPLETED)
         Intent(context, AlarmService::class.java).also {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                context?.startForegroundService(it)
             else
                 context?.startService(it)
         }
    }
}