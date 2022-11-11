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
        val formatterDate = SimpleDateFormat("dd-MMMM-yyyy EEE, HH:mm", Locale("ru"))

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