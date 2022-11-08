package com.example.table.utils

import android.os.Build
import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

class ConverterUtils {

    companion object{

        val formatter = SimpleDateFormat("EEE, HH:mm", Locale("ru"))
        val formatterTime = SimpleDateFormat("HH:mm", Locale("ru"))

    }

    @TypeConverter
    fun fromString(str: String): Date {
        return formatter.parse(str)
    }

    @TypeConverter
    fun fromDate(date: Date): String{
        return formatter.format(date)
    }
}