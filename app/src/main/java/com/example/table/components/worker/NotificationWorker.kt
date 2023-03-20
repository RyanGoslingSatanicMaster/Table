package com.example.table.components.worker

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.provider.Settings
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LifecycleService
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.table.R
import com.example.table.components.TableApp
import com.example.table.components.activity.MainActivity
import com.example.table.components.broadcasts.AlarmReceiver
import com.example.table.di.components.DaggerNotificationWorkerComponent
import com.example.table.di.components.NotificationWorkerComponent
import com.example.table.di.modules.ApplicationModule
import com.example.table.di.modules.RoomModule
import com.example.table.di.modules.WorkerModule
import com.example.table.model.db.Group
import com.example.table.model.pojo.TimeTableWithLesson
import com.example.table.model.requests.NextLessonRequest
import com.example.table.usecases.GetNextLessonTime
import com.example.table.usecases.IGetActiveGroup
import com.example.table.usecases.IGetTimeTable
import com.example.table.utils.ConverterUtils
import com.example.table.utils.PrefUtils
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

class NotificationWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    lateinit var workerComponent: NotificationWorkerComponent

    @Inject
    lateinit var getActiveGroup: IGetActiveGroup

    @Inject
    lateinit var getNextLessonTime: GetNextLessonTime

    @Inject
    lateinit var alarmManager: AlarmManager

    @Inject
    lateinit var prefUtils: PrefUtils

    override suspend fun doWork(): Result {
        inject()
        val group = try {
            getActiveGroup.getActiveGroup()
        }catch (ex: java.lang.Exception){
            return Result.retry()
        } ?: return Result.success()

        val settings = prefUtils.getNotifications()
        var lesson: TimeTableWithLesson? = null
        var testTime = Calendar.getInstance()
        for (i in 0..prefUtils.getTestKeyTime()) {
            testTime.add(Calendar.MINUTE, settings.third)
            lesson = try {
                getNextLessonTime.getNextLessonTime(NextLessonRequest(settings, group, testTime.time))
            } catch (ex: java.lang.Exception) {
                return Result.retry()
            }
            testTime.time = lesson?.timeTable?.time!!
        }
        prefUtils.incTestKey()
        showNotification(lesson!!)

        setNextNotification(group, settings)

        return Result.success()
    }

    fun inject(){
        workerComponent = DaggerNotificationWorkerComponent.builder()
            .workerModule(WorkerModule(this))
            .roomModule(RoomModule(applicationContext))
            .build()
        workerComponent.inject(this)
    }

    fun showNotification(lesson: TimeTableWithLesson){
        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.table_logo)
            .setContentTitle("Новое сообщение")
            .setContentText("Получено")
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
            .setAutoCancel(true)
            .setContent(createNotificationView(lesson))
        val notification = builder.build()
        notification.flags.or(Notification.FLAG_AUTO_CANCEL)
        val mNotificationManager =
            applicationContext.getSystemService(LifecycleService.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(CHANNEL_ID,
                "TimeTableAppChannel",
                NotificationManager.IMPORTANCE_DEFAULT)
            mNotificationManager.createNotificationChannel(channel)

        }
        with(mNotificationManager){
            notify(NOTIFICATION_ID, notification)
        }
    }

    fun createNotificationView(lesson: TimeTableWithLesson): RemoteViews{
        val teacherString = ""
        lesson.lesson.teachers.forEach {
            teacherString.plus(it.teacherName)
        }
        val remoteView = RemoteViews(applicationContext.packageName, R.layout.notification_item)
        remoteView.setTextViewText(R.id.time, ConverterUtils.formatterTime.format(lesson.timeTable.time))
        remoteView.setTextViewText(R.id.is_lection, if (lesson.lesson.lesson.isLection) "Лекция" else "Практика")
        remoteView.setTextViewText(R.id.lesson_name, lesson.lesson.lesson.lessonName)
        remoteView.setTextViewText(R.id.cabinet, lesson.timeTable.cabinet)
        remoteView.setTextViewText(R.id.teachers, teacherString)
        return remoteView
    }

    suspend fun setNextNotification(group: Group, settings: Triple<Boolean, Boolean, Int>){
        val alarmPendingIntent = Intent(applicationContext, AlarmReceiver::class.java).let {
            it.action = MainActivity.ALARM_ACTION
            PendingIntent.getBroadcast(applicationContext, 0, it, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }
        val cal = Calendar.getInstance()
        cal.add(Calendar.MINUTE, settings.third)
        val nextLesson = getNextLessonTime.getNextLessonTime(NextLessonRequest(settings, group, cal.time))
        val next = alarmManager.nextAlarmClock
        if (next != null)
            alarmManager.cancel(alarmPendingIntent)
        alarmManager.setAlarmClock(
            AlarmManager.AlarmClockInfo(
                System.currentTimeMillis() + (60 * 1000),
                alarmPendingIntent),
            alarmPendingIntent
        )
    }

    companion object{
        const val NOTIFICATION_ID = 100
        const val CHANNEL_ID = "ALARM_CHANNEL_TIMETABLE_APP"
    }
}
