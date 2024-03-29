package com.doggystyle.table.utils

import android.content.SharedPreferences
import com.doggystyle.table.annotations.PerActivity
import com.doggystyle.table.components.activity.MainActivity
import com.doggystyle.table.model.requests.NextLessonRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrefUtils @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    fun getNotifications(): Triple<Boolean, Boolean, Int>{
        return Triple(sharedPreferences.getBoolean(LECTION_NOTIFY_KEY, false),
                sharedPreferences.getBoolean(PRACTICE_NOTIFY_KEY, false), sharedPreferences.getInt(
                    TIME_BEFORE_NOTIFY_KEY, 5))
    }

    fun setNotifications(pair: Triple<Boolean, Boolean, Int>){
        with(sharedPreferences.edit()){
            putBoolean(LECTION_NOTIFY_KEY, pair.first)
            putBoolean(PRACTICE_NOTIFY_KEY, pair.second)
            putInt(TIME_BEFORE_NOTIFY_KEY, pair.third)
            apply()
        }
    }

    fun isAppFirstStart(): Boolean{
        val isAppFirstStart = sharedPreferences.getBoolean(APP_FIRST_START_KEY, true)
        if (isAppFirstStart)
            with(sharedPreferences.edit()){
                putBoolean(APP_FIRST_START_KEY, false)
                apply()
            }
        return isAppFirstStart
    }

    fun getTestKeyTime(): Int{
        return sharedPreferences.getInt(TEST_KEY_TIME, 0)
    }

    fun incTestKey(){
        with(sharedPreferences.edit()){
            putInt(TEST_KEY_TIME, sharedPreferences.getInt(TEST_KEY_TIME, 0) + 1)
            apply()
        }
    }

    companion object {
        const val LECTION_NOTIFY_KEY = "LECTION_NOTIFY_KEY"
        const val PRACTICE_NOTIFY_KEY = "PRACTICE_NOTIFY_KEY"
        const val TIME_BEFORE_NOTIFY_KEY = "TIME_BEFORE_NOTIFY_KEY"
        const val TEST_KEY_TIME = "TEST_KEY_TIME"
        const val APP_FIRST_START_KEY = "APP_FIRST_START_KEY"
    }
}
