package com.example.table.components.worker

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
import com.example.table.model.db.Teacher
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
            .setCustomContentView(createCollapsedNotificationView(lesson))
            .setCustomBigContentView(createCustomNotificationView(lesson))
            .setCustomHeadsUpContentView(createCollapsedNotificationView(lesson))
            .setDefaults(DEFAULT_SOUND)
            .setPriority(Notification.PRIORITY_MAX)
        val notification = builder.build()
        notification.flags.or(Notification.FLAG_AUTO_CANCEL)
        val mNotificationManager =
            applicationContext.getSystemService(LifecycleService.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(CHANNEL_ID,
                "Уведомления о парах",
                NotificationManager.IMPORTANCE_DEFAULT)
            mNotificationManager.createNotificationChannel(channel)

        }
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
        val cal = Calendar.getInstance()
        cal.add(Calendar.MINUTE, settings.third)
        val nextLesson = getNextLessonTime.getNextLessonTime(NextLessonRequest(settings, group, cal.time))
        val next = alarmManager.nextAlarmClock
        if (next != null)
            alarmManager.cancel(alarmPendingIntent)
        alarmManager.setAlarmClock(
            AlarmManager.AlarmClockInfo(
                nextLesson.timeTable.time.time,
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
    }
}
