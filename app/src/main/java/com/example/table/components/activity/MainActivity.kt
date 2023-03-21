package com.example.table.components.activity

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.os.SystemClock
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.table.components.TableApp
import com.example.table.components.broadcasts.AlarmReceiver
import com.example.table.components.fragments.GroupSelectionFragment
import com.example.table.components.fragments.SettingsFragment
import com.example.table.components.fragments.TimeTableFragment
import com.example.table.di.DaggerViewModelFactory
import com.example.table.di.components.MainActivityComponent
import com.example.table.di.modules.ActivityModule
import com.example.table.model.pojo.TimeTableWithLesson
import com.example.table.model.requests.NextLessonRequest
import com.example.table.ui.ComposeFragmentContainer
import com.example.table.ui.FragmentController
import com.example.table.ui.theme.TableTheme
import com.example.table.ui.theme.isGestureNavigationMode
import com.example.table.utils.PrefUtils
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import java.util.*
import javax.inject.Inject
import javax.inject.Provider


class MainActivity : AppCompatActivity() {

    //TODO Get link on lesson
    //TODO pending intent notification
    //TODO Settings: Time before notificate, Saved Groups,
    //TODO Bug: correct navigation, correct display next screen

    @Inject
    lateinit var factory: DaggerViewModelFactory

    @Inject
    lateinit var prefUtils: PrefUtils

    @Inject
    lateinit var fragmentMap: Map<Class<out Fragment>, @JvmSuppressWildcards(true) Provider<Fragment>>

    @Inject
    lateinit var alarmManager: AlarmManager

    lateinit var viewModel: MainViewModel

    lateinit var activityComponent: MainActivityComponent

    lateinit var alarmPendingIntent: PendingIntent



    val fragmentContainerId: Int = View.generateViewId()


    companion object{
        const val NOTIFY_KEY = "NOTIFY_KEY"
        const val ALARM_ACTION = "ALARM_ACTION_TIMETABLE_APP"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        if (isGestureNavigationMode(this.contentResolver))
            WindowCompat.setDecorFitsSystemWindows(window, false)
        alarmPendingIntent = Intent(this, AlarmReceiver::class.java).let {
            it.action = ALARM_ACTION
            PendingIntent.getBroadcast(this, 0, it, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        activityComponent = (application as TableApp).appComponent.getMainActivityComponent(
            ActivityModule(this)
        )
        activityComponent.inject(this)

        viewModel = ViewModelProvider(this, factory).get(MainViewModel::class.java)

        val activeGroup = intent.getBooleanExtra(SplashScreenActivity.ACTIVE_GROUP_TAG, false)

        alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        getNotifications()

        viewModel.notificationSettings.observe(this){ triple ->
            if (triple != null)
                viewModel.activeGroup.value?.let {
                    if (it.isActive && it.dateOfFirstWeek != null)
                        viewModel.getNextLessonTime(NextLessonRequest(triple, it))
                }
        }

        viewModel.nextLessonTime.observe(this){
            setNewAlarm(it)
        }

        viewModel.activeGroup.observe(this){ group ->
            Log.v("ACTIVE_GROUP", group.toString())
            if (group != null)
                viewModel.notificationSettings.value?.let {
                    if (group.isActive && group.dateOfFirstWeek != null)
                        viewModel.getNextLessonTime(NextLessonRequest(it, group))
                }
        }

        onBackPressedDispatcher.addCallback(this){
            lastBackStackFragment()
        }

        setContent {
            TableTheme {
                transparentStatusBar()
                ComposeFragmentContainer(viewId = fragmentContainerId, fragmentManager = this.supportFragmentManager
                ) {
                    when{
                        activeGroup && viewModel.activeGroup.value == null -> {
                            viewModel.getActiveGroup()
                            add(it, fragmentMap.get(TimeTableFragment::class.java)!!.get())
                        }
                        // TODO controversial decision, may be replace with another features
                        viewModel.activeGroup.value != null -> {
                            add(it, fragmentMap.get(TimeTableFragment::class.java)!!.get())
                        }
                        else -> {
                            add(it, fragmentMap.get(GroupSelectionFragment::class.java)!!.get())
                        }
                    }
                }

            }
        }

    }

    @Composable
    fun transparentStatusBar(){
        val systemUiController = rememberSystemUiController()
        SideEffect {
            systemUiController.setStatusBarColor(
                color = Transparent,
                darkIcons = false
            )
        }
    }

    fun requestPermission(perm: String, callback: () -> Unit){
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
            val permission = ActivityCompat.checkSelfPermission(
                this,
                perm
            )
            if (permission == PackageManager.PERMISSION_GRANTED)
                callback.invoke()
            else
                returnNotifySettings()
        }
        else
            callback.invoke()
    }

    private fun returnNotifySettings(){
        Toast.makeText(
            this,
            "Разрешение на использование будильника отключено",
            Toast.LENGTH_LONG
        )
        viewModel.notificationSettings.value = Triple(false, false, 5)
    }

    private fun setNewAlarm(nextLessonTime: TimeTableWithLesson){
        val next = alarmManager.nextAlarmClock
        if (next != null)
            alarmManager.cancel(alarmPendingIntent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            if (alarmManager.canScheduleExactAlarms())
                alarmManager.setAlarmClock(
                    AlarmManager.AlarmClockInfo(
                        nextLessonTime.timeTable.time.time,
                        alarmPendingIntent),
                    alarmPendingIntent
                )
            else
                returnNotifySettings()
        else
            alarmManager.setAlarmClock(
                AlarmManager.AlarmClockInfo(
                    nextLessonTime.timeTable.time.time,
                    alarmPendingIntent),
                alarmPendingIntent
            )
    }

    private fun getNotifications(){
        viewModel.notificationSettings.value = prefUtils.getNotifications()
    }

    fun setNotifications(pair: Triple<Boolean, Boolean, Int>){
        viewModel.notificationSettings.value = pair
        prefUtils.setNotifications(pair)
        viewModel.getNextLessonTime(NextLessonRequest(pair, viewModel.activeGroup.value!!))
    }

    fun startTimeTableFragment(){
        FragmentController(fragmentContainerId, fragmentMap.get(TimeTableFragment::class.java)!!.get(), supportFragmentManager)
    }

    fun startGroupSelectionFragment(){
        FragmentController(fragmentContainerId, fragmentMap.get(GroupSelectionFragment::class.java)!!.get(), supportFragmentManager)
    }

    fun startSettingsFragment(){
        FragmentController(fragmentContainerId, fragmentMap.get(SettingsFragment::class.java)!!.get(), supportFragmentManager)
    }

    private fun lastBackStackFragment(){
        val stackSize = supportFragmentManager.backStackEntryCount
        if (stackSize != 0)
            supportFragmentManager.popBackStack()
        else
            finish()
    }

}
