package com.doggystyle.table.components.activity

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.doggystyle.table.components.RefreshCallback
import com.doggystyle.table.components.TableApp
import com.doggystyle.table.components.broadcasts.AlarmReceiver
import com.doggystyle.table.components.broadcasts.TimeTableWidgetReceiver
import com.doggystyle.table.components.fragments.GroupSelectionFragment
import com.doggystyle.table.components.fragments.SettingsFragment
import com.doggystyle.table.components.fragments.TimeTableFragment
import com.doggystyle.table.components.worker.NotificationWorker
import com.doggystyle.table.di.DaggerViewModelFactory
import com.doggystyle.table.di.components.MainActivityComponent
import com.doggystyle.table.di.modules.ActivityModule
import com.doggystyle.table.model.pojo.TimeTableWithLesson
import com.doggystyle.table.model.requests.NextLessonRequest
import com.doggystyle.table.ui.ComposeFragmentContainer
import com.doggystyle.table.ui.FragmentController
import com.doggystyle.table.ui.theme.TableTheme
import com.doggystyle.table.ui.theme.isGestureNavigationMode
import com.doggystyle.table.utils.PrefUtils
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import java.util.*
import javax.inject.Inject
import javax.inject.Provider
import kotlin.properties.Delegates


class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: DaggerViewModelFactory

    @Inject
    lateinit var prefUtils: PrefUtils

    @Inject
    lateinit var fragmentMap: Map<Class<out Fragment>, @JvmSuppressWildcards(true) Provider<Fragment>>

    @Inject
    lateinit var alarmManager: AlarmManager

    lateinit var viewModel: MainViewModel

    private lateinit var activityComponent: MainActivityComponent

    lateinit var alarmPendingIntent: PendingIntent

    private val isFromNotification by lazy {
        intent.getBooleanExtra(NotificationWorker.ACTION_NOTIFICATION_INTENT, false)
    }

    private val permLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        permissionNotification = it
        if (!it)
            returnNotifySettings()
    }


    private var permissionNotification by Delegates.notNull<Boolean>()

    val fragmentContainerId: Int = View.generateViewId()


    companion object {
        const val NOTIFY_KEY = "NOTIFY_KEY"
        const val ALARM_ACTION = "ALARM_ACTION_TIMETABLE_APP"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (isGestureNavigationMode(this.contentResolver))
            WindowCompat.setDecorFitsSystemWindows(window, false)

        permissionNotification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            ActivityCompat.checkSelfPermission(this,
                Manifest.permission.SCHEDULE_EXACT_ALARM) == PackageManager.PERMISSION_GRANTED else true

        alarmPendingIntent = Intent(this, AlarmReceiver::class.java).let {
            it.action = ALARM_ACTION
            PendingIntent.getBroadcast(this,
                0,
                it,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        activityComponent = (application as TableApp).appComponent.getMainActivityComponent(
            ActivityModule(this)
        )
        activityComponent.inject(this)

        viewModel = ViewModelProvider(this, factory).get(MainViewModel::class.java)

        val activeGroup = intent.getBooleanExtra(SplashScreenActivity.ACTIVE_GROUP_TAG, false)

        alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        getNotifications()

        initSubscribes()

        onBackPressedDispatcher.addCallback(this) {
            lastBackStackFragment()
        }

        setContent {
            TableTheme {
                transparentStatusBar()
                ComposeFragmentContainer(viewId = fragmentContainerId,
                    fragmentManager = this.supportFragmentManager
                ) {
                    when {
                        activeGroup && viewModel.activeGroup.value == null || isFromNotification -> {
                            viewModel.getActiveGroup()
                            add(it, fragmentMap[TimeTableFragment::class.java]!!.get())
                        }
                        // TODO controversial decision, may be replace with another features
                        viewModel.activeGroup.value != null -> {
                            add(it, fragmentMap[TimeTableFragment::class.java]!!.get())
                        }

                        else -> {
                            add(it, fragmentMap[GroupSelectionFragment::class.java]!!.get())
                        }
                    }
                }

            }
        }

    }

    fun initSubscribes() {
        viewModel.notificationSettings.observe(this) { triple ->
            if (triple != null) {
                if (triple.first || triple.second)
                    viewModel.activeGroup.value?.let {
                        if (it.isActive && it.dateOfFirstWeek != null)
                            viewModel.getNextLessonTime(NextLessonRequest(triple, it),
                                ::setNewAlarm)
                    }
                else
                    cancelNextAlarm()
            }
        }

        viewModel.activeGroup.observe(this) { group ->
            if (group != null) {
                val intent = Intent(this, TimeTableWidgetReceiver::class.java).apply {
                    action = RefreshCallback.UPDATE_ACTION
                }
                sendBroadcast(intent)
                viewModel.notificationSettings.value?.let {
                    if (it.first || it.second) {
                        if (group.isActive && group.dateOfFirstWeek != null)
                            viewModel.getNextLessonTime(NextLessonRequest(it, group), ::setNewAlarm)
                    } else
                        cancelNextAlarm()
                }
            }
        }
    }

    @Composable
    fun transparentStatusBar() {
        val systemUiController = rememberSystemUiController()
        SideEffect {
            systemUiController.setStatusBarColor(
                color = Transparent,
                darkIcons = true
            )
        }
    }

    fun requestPermission(callback: () -> Unit) {
        /*val intent = Intent()
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        if (powerManager.isIgnoringBatteryOptimizations(packageName))
            intent.action = Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
        else {
            intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            intent.data = Uri.parse("package:$packageName")
        }
        startActivity(intent)*/
        if (Build.VERSION.SDK_INT >= 31) {
            if (permissionNotification)
                callback.invoke()
            else {
                permLauncher.launch(Manifest.permission.SCHEDULE_EXACT_ALARM)
                callback.invoke()
            }
        } else
            callback.invoke()
    }

    private fun returnNotifySettings() {
        Toast.makeText(
            this,
            "Разрешение на использование будильника отключено",
            Toast.LENGTH_LONG
        ).show()
        val triple = Triple(false, false, 5)
        viewModel.notificationSettings.value = triple
        prefUtils.setNotifications(triple)
    }

    fun navigateWebView(url: String) {
        val httpIntent = Intent(Intent.ACTION_VIEW)
        httpIntent.data = Uri.parse(url)
        startActivity(httpIntent)
    }

    private fun cancelNextAlarm() {
        val next = alarmManager.nextAlarmClock
        if (next != null)
            alarmManager.cancel(alarmPendingIntent)
    }

    private fun setNewAlarm(
        nextLessonTime: TimeTableWithLesson,
        settings: Triple<Boolean, Boolean, Int>,
    ) {
        val next = alarmManager.nextAlarmClock
        val cal = Calendar.getInstance().apply {
            time = nextLessonTime.timeTable.time
            add(Calendar.MINUTE, settings.third * -1)
        }
        if (next != null)
            alarmManager.cancel(alarmPendingIntent)
        if (!settings.first && !settings.second)
            return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            if (alarmManager.canScheduleExactAlarms())
                alarmManager.setAlarmClock(
                    AlarmManager.AlarmClockInfo(
                        cal.timeInMillis,
                        alarmPendingIntent),
                    alarmPendingIntent
                )
            else {
                returnNotifySettings()
                Intent().also { intent ->
                    intent.action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                    startActivity(intent)
                }
            }
        else
            alarmManager.setAlarmClock(
                AlarmManager.AlarmClockInfo(
                    cal.timeInMillis,
                    alarmPendingIntent),
                alarmPendingIntent
            )

    }

    private fun getNotifications() {
        viewModel.notificationSettings.value = prefUtils.getNotifications()
    }

    fun setNotifications(pair: Triple<Boolean, Boolean, Int>) {
        viewModel.notificationSettings.value = pair
        prefUtils.setNotifications(pair)
    }

    fun startTimeTableFragment() {
        FragmentController(fragmentContainerId,
            fragmentMap.get(TimeTableFragment::class.java)!!.get(),
            supportFragmentManager)
    }

    fun startGroupSelectionFragment() {
        FragmentController(fragmentContainerId,
            fragmentMap.get(GroupSelectionFragment::class.java)!!.get(),
            supportFragmentManager)
    }

    fun startSettingsFragment() {
        FragmentController(fragmentContainerId,
            fragmentMap.get(SettingsFragment::class.java)!!.get(),
            supportFragmentManager)
    }

    private fun lastBackStackFragment() {
        val stackSize = supportFragmentManager.backStackEntryCount
        if (stackSize != 0)
            supportFragmentManager.popBackStack()
        else
            finishAffinity()
    }

}
