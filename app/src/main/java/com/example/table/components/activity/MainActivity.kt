package com.example.table.components.activity

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.table.R
import com.example.table.components.fragments.GroupSelectionFragment
import com.example.table.components.TableApp
import com.example.table.components.broadcasts.AlarmReceiver
import com.example.table.components.fragments.SettingsFragment
import com.example.table.components.fragments.TimeTableFragment
import com.example.table.di.DaggerViewModelFactory
import com.example.table.di.components.MainActivityComponent
import com.example.table.di.modules.ActivityModule
import com.example.table.model.requests.NextLessonRequest
import com.example.table.ui.ComposeFragmentContainer
import com.example.table.ui.FragmentController
import com.example.table.ui.theme.TableTheme
import javax.inject.Inject
import javax.inject.Provider

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: DaggerViewModelFactory

    @Inject
    lateinit var sharedPref: SharedPreferences

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
        const val LECTION_NOTIFY_KEY = "LECTION_NOTIFY_KEY"
        const val PRACTICE_NOTIFY_KEY = "PRACTICE_NOTIFY_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        alarmPendingIntent = Intent(this, AlarmReceiver::class.java).let {
            PendingIntent.getBroadcast(this, 0, it, 0)
        }

        activityComponent = (application as TableApp).appComponent.getMainActivityComponent(
            ActivityModule(this)
        )
        activityComponent.inject(this)

        viewModel = ViewModelProvider(this, factory).get(MainViewModel::class.java)

        val activeGroup = intent.getBooleanExtra(SplashScreenActivity.ACTIVE_GROUP_TAG, false)

        alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        getNotifications()

        viewModel.notificationSettings.observe(this){
            alarmManager.nextAlarmClock
        }

        onBackPressedDispatcher.addCallback(this){
            lastBackStackFragment()
        }

        setContent {
            TableTheme {
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

    private fun getNotifications(){
        viewModel.notificationSettings.value = sharedPref.getBoolean(LECTION_NOTIFY_KEY, false) to
                sharedPref.getBoolean(PRACTICE_NOTIFY_KEY, false)
    }

    fun setNotifications(pair: Pair<Boolean, Boolean>){
        viewModel.notificationSettings.value = pair
        with(sharedPref.edit()){
            putBoolean(LECTION_NOTIFY_KEY, pair.first)
            putBoolean(PRACTICE_NOTIFY_KEY, pair.second)
            apply()
        }
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
