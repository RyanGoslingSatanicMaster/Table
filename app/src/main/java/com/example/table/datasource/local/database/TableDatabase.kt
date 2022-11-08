package com.example.table.datasource.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.table.datasource.local.dao.TimeTableDao
import com.example.table.model.db.*
import com.example.table.utils.ConverterUtils

@Database(entities = [Group::class, Lesson::class, TimeTable::class, Teacher::class, LessonTeacherCrossRef::class], version = TableDatabase.VERSION, exportSchema = false)
@TypeConverters(ConverterUtils::class)
abstract class TableDatabase: RoomDatabase() {

    abstract fun getTimeTableDAO(): TimeTableDao

    companion object{

        const val VERSION = 1
    }
}