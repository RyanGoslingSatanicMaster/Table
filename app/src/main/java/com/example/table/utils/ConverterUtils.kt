package com.example.table.utils

import android.os.Build
import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.format.DateTimeFormatter
import java.util.*

class ConverterUtils {

    companion object{

        val formatter = SimpleDateFormat("EEE, HH:mm", Locale("ru"))
        val formatterTime = SimpleDateFormat("HH:mm", Locale("ru"))
        val formatterDay = SimpleDateFormat("EEE", Locale("ru"))
        val formatterDate = SimpleDateFormat("dd-MMMM-yyyy EEE, HH:mm", Locale("ru"))

        fun isFirstWeek(date: Date, currentDate: Date): Boolean{
            val cal = Calendar.getInstance()
            cal.time = date
            val cal2 = Calendar.getInstance()
            cal2.time = currentDate
            val daysBetween = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Duration.between(cal.toInstant(), cal2.toInstant()).toDays()
            } else {
                (cal2.timeInMillis)/60/60/24 - (cal2.timeInMillis)/60/60/24
            }
            return ((daysBetween/7)%2 == 0L)
        }

    }

    @TypeConverter
    fun fromString(str: String): Date {
        return formatterDate.parse(str)
    }

    @TypeConverter
    fun fromDate(date: Date): String{
        return formatterDate.format(date)
    }
}