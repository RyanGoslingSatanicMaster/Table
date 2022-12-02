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
        val formatterDate = SimpleDateFormat("dd-MM-yyyy EEE, HH:mm", Locale("ru"))
        private val PREFIX_WEEK = mapOf(
            "Понедельник" to "07-11-2022 ",
            "Вторник" to "08-11-2022 ",
            "Среда" to "09-11-2022 ",
            "Четверг" to "10-11-2022 ",
            "Пятница" to "11-11-2022 ",
            "Суббота" to "12-11-2022 ",
            "Воскресение" to "13-11-2022 "
        )

        fun isFirstWeek(date: Date, currentDate: Date): Boolean{
            val cal = Calendar.getInstance()
            cal.time = getFirstDayOfWeek(date)
            val cal2 = Calendar.getInstance()
            cal2.time = currentDate
            val daysBetween = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Duration.between(cal.toInstant(), cal2.toInstant()).toDays()
            } else {
                Math.abs((cal2.timeInMillis)/60/60/24 - (cal2.timeInMillis)/60/60/24)
            }
            return ((daysBetween/7)%2 == 0L)
        }

        fun getFirstDayOfWeek(date: Date): Date{
            val cal = Calendar.getInstance()
            when(date.day){
                0 -> cal.set(date.year, date.month, date.date - 6)
                1 -> return date
                2 -> cal.set(date.year, date.month, date.date - 1)
                3 -> cal.set(date.year, date.month, date.date - 2)
                4 -> cal.set(date.year, date.month, date.date - 3)
                5 -> cal.set(date.year, date.month, date.date - 4)
                6 -> cal.set(date.year, date.month, date.date - 5)
            }
            return cal.time
        }

        fun parseDateWithPrefix(str: String): Date{
            return when{
                str.contains("Понедельник") || str.contains("пн") -> formatterDate.parse(PREFIX_WEEK["Понедельник"] + str)
                str.contains("Вторник") || str.contains("вт") -> formatterDate.parse(PREFIX_WEEK["Вторник"] + str)
                str.contains("Среда") || str.contains("ср") -> formatterDate.parse(PREFIX_WEEK["Среда"] + str)
                str.contains("Четверг") || str.contains("чт")  -> formatterDate.parse(PREFIX_WEEK["Четверг"] + str)
                str.contains("Пятница") || str.contains("пт")  -> formatterDate.parse(PREFIX_WEEK["Пятница"] + str)
                str.contains("Суббота") || str.contains("сб") -> formatterDate.parse(PREFIX_WEEK["Суббота"] + str)
                else -> formatterDate.parse(PREFIX_WEEK["Воскресение"] + str)
            }
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