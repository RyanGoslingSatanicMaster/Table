package com.example.table.datasource.local.database

import android.content.Context
import android.content.pm.PackageManager
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.table.datasource.local.dao.TimeTableDao
import com.example.table.model.db.Group
import com.example.table.model.db.TimeTable
import com.example.table.model.requests.NextLessonRequest
import com.example.table.utils.ConverterUtils
import com.example.table.utils.timeTableDeserialization
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*

@RunWith(AndroidJUnit4::class)
class TableDatabaseTest: TestCase() {

    private lateinit var db: TableDatabase

    private lateinit var dao: TimeTableDao

    private lateinit var currentDate: Date

    @Before
    public override fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, TableDatabase::class.java).build()
        dao = db.getTimeTableDAO()
        currentDate = Date()
        writeData()
    }

    @After
    public fun closeDb(){

    }

    fun writeData() = runBlocking {
        var inputStream: InputStream? = null
        var html: String? = null
        try {
            inputStream =
                javaClass.classLoader?.getResourceAsStream("test.html")
            val reader = BufferedReader(InputStreamReader(inputStream))
            html = reader.use { it.readText() }
        }finally {
            inputStream?.close()
        }
        html?.let {
            val timeTable = timeTableDeserialization(html, Group(groupName = "ИТ1901", isActive = true, dateOfFirstWeek = currentDate))
            timeTable.forEach { println(ConverterUtils.formatter.format(it.timeTable.time)) }
            dao.saveAllTimeTableWithLesson(timeTable)
        }
    }

    @Test
    fun getNextTimeTest()= runBlocking {
        val next = dao.getNextDayLessonsTime(NextLessonRequest(day = "пн", true to true, true))
        assert(ConverterUtils.formatterTime.format(next.sortedBy { it }.get(0))=="11:30")
    }

    @Test
    fun deleteGroupDataTest() = runBlocking {
        dao.deleteTimeTableOfGroup(1)
        assert(dao.getAllTimeTable().isEmpty())
        assert(dao.getAllGroup().isEmpty())
        assert(dao.getAllCrossRef().isEmpty())
        assert(dao.getAllTimeTable().isEmpty())
        assert(dao.getAllTeacher().isEmpty())
        assert(dao.getAllLesson().isEmpty())
    }


}