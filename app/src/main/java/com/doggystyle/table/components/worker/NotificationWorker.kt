package com.doggystyle.table.components.worker

import android.app.*
import android.app.Notification.DEFAULT_SOUND
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
import com.doggystyle.table.R
import com.doggystyle.table.components.TableApp
import com.doggystyle.table.components.activity.MainActivity
import com.doggystyle.table.components.broadcasts.AlarmReceiver
import com.doggystyle.table.di.components.DaggerNotificationWorkerComponent
import com.doggystyle.table.di.components.NotificationWorkerComponent
import com.doggystyle.table.di.modules.ApplicationModule
import com.doggystyle.table.di.modules.RoomModule
import com.doggystyle.table.di.modules.WorkerModule
import com.doggystyle.table.model.db.Group
import com.doggystyle.table.model.db.Teacher
import com.doggystyle.table.model.pojo.TimeTableWithLesson
import com.doggystyle.table.model.requests.NextLessonRequest
import com.doggystyle.table.usecases.GetNextLessonTime
import com.doggystyle.table.usecases.IGetActiveGroup
import com.doggystyle.table.usecases.IGetTimeTable
import com.doggystyle.table.utils.ConverterUtils
import com.doggystyle.table.utils.PrefUtils
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
        var currentTime = Calendar.getInstance()
        lesson = try {
            getNextLessonTime.getNextLessonTime(NextLessonRequest(settings, group, currentTime.time))
        } catch (ex: java.lang.Exception) {
            return Result.retry()
        }
        /*for (i in 0..prefUtils.getTestKeyTime()) {
            testTime.add(Calendar.MINUTE, settings.third)
            lesson = try {
                getNextLessonTime.getNextLessonTime(NextLessonRequest(settings, group, testTime.time))
            } catch (ex: java.lang.Exception) {
                return Result.retry()
            }
            testTime.time = lesson?.timeTable?.time!!
        }
        prefUtils.incTestKey()*/
        showNotification(lesson!!)

        setNextNotification(group, settings)

        return Result.success()
    }

    fun inject(){
        workerComponent = com.doggystyle.table.di.components.DaggerNotificationWorkerComponent.builder()
            .workerModule(WorkerModule(this))
            .roomModule(RoomModule(applicationContext))
            .build()
        workerComponent.inject(this)
    }

    fun showNotification(lesson: TimeTableWithLesson){
        val notifyIntent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(ACTION_NOTIFICATION_INTENT, true)
        }
        val notifyPendingIntent = PendingIntent.getActivity(
            applicationContext, 0, notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.table_logo)
            .setContentTitle("Новое сообщение")
            .setContentText("Получено")
            .setContentIntent(notifyPendingIntent)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
            .setAutoCancel(true)
            .setCustomContentView(createCollapsedNotificationView(lesson))
            .setCustomBigContentView(createCustomNotificationView(lesson))
            .setCustomHeadsUpContentView(createCollapsedNotificationView(lesson))
            .setDefaults(DEFAULT_SOUND)
            .setPriority(Notification.PRIORITY_MAX)
        val notification = builder.build()
        notification.flags.or(Notification.FLAG_AUTO_CANCEL)
        val mNotificationManager =
            applicationContext.getSystemService(LifecycleService.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(CHANNEL_ID,
            "Уведомления о парах",
            NotificationManager.IMPORTANCE_DEFAULT)
        mNotificationManager.createNotificationChannel(channel)

        with(mNotificationManager){
            notify(NOTIFICATION_ID, notification)
        }
    }

    fun createCustomNotificationView(lesson: TimeTableWithLesson): RemoteViews{
        val expanded = RemoteViews(applicationContext.packageName, R.layout.notification_expanded)
        expanded.setTextViewText(R.id.time_lesson, ConverterUtils.formatterTime.format(lesson.timeTable.time))
        expanded.setTextViewText(R.id.type_lesson, if (lesson.lesson.lesson.isLection) "Лекция" else "Практика")
        expanded.setTextViewText(R.id.name_lesson, lesson.lesson.lesson.lessonName)
        expanded.setTextViewText(R.id.teachers, lesson.lesson.teachers.convertToString())
        expanded.setTextViewText(R.id.lesson_cabinet, lesson.timeTable.cabinet)
        return expanded
    }

    fun createCollapsedNotificationView(lesson: TimeTableWithLesson): RemoteViews{
        val collapsed = RemoteViews(applicationContext.packageName, R.layout.notification_collapsed)
        collapsed.setTextViewText(R.id.collapsed_notification_title, ConverterUtils.formatterTime.format(lesson.timeTable.time))
        collapsed.setTextViewText(R.id.collapsed_notification_info, lesson.lesson.lesson.lessonName.convertLessonName())
        return collapsed
    }

    suspend fun setNextNotification(group: Group, settings: Triple<Boolean, Boolean, Int>){
        val alarmPendingIntent = Intent(applicationContext, AlarmReceiver::class.java).let {
            it.action = MainActivity.ALARM_ACTION
            PendingIntent.getBroadcast(applicationContext, 0, it, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }
        val cal = Calendar.getInstance().apply {
            add(Calendar.MINUTE, settings.third)
        }
        val nextLesson = getNextLessonTime.getNextLessonTime(NextLessonRequest(settings, group, cal.time))
        val calNextLesson = Calendar.getInstance().apply {
            time = nextLesson.timeTable.time
            add(Calendar.MINUTE, settings.third * -1)
        }
        val next = alarmManager.nextAlarmClock
        if (next != null)
            alarmManager.cancel(alarmPendingIntent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            if (alarmManager.canScheduleExactAlarms())
                alarmManager.setAlarmClock(
                    AlarmManager.AlarmClockInfo(
                        calNextLesson.timeInMillis,
                        alarmPendingIntent),
                    alarmPendingIntent
                )

        else
            alarmManager.setAlarmClock(
                AlarmManager.AlarmClockInfo(
                    calNextLesson.timeInMillis,
                    alarmPendingIntent),
                alarmPendingIntent
            )
    }

    fun String.convertLessonName(): String{
        if (length > 30)
            replaceRange(31, length - 1, "...")
        return this
    }

    fun List<Teacher>.convertToString(): String{
        var str = ""
        forEachIndexed { index, el ->
            if (index != size - 1)
                str += el.teacherName + ", "
            else
                str += el.teacherName
        }
        return str
    }

    companion object{
        const val NOTIFICATION_ID = 100
        const val CHANNEL_ID = "ALARM_CHANNEL_TIMETABLE_APP"
        const val ACTION_NOTIFICATION_INTENT = "ACTION_NOTIFICATION_INTENT"
    }
}
