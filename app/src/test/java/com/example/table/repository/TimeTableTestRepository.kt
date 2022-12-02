package com.example.table.repository

import com.example.table.model.db.Group
import com.example.table.model.pojo.TimeTableWithLesson
import com.example.table.model.requests.TimeTableRequest
import com.example.table.repositories.ITimeTableRepository
import com.example.table.utils.ConverterUtils
import com.example.table.utils.timeTableDeserialization
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*

class TimeTableTestRepository: ITimeTableRepository {

    private val dataSoure: List<TimeTableWithLesson>

    init {
        var inputStream: InputStream? = null
        var html: String? = null
        try {
            inputStream = javaClass.classLoader?.getResourceAsStream("test.html")
            val reader = BufferedReader(InputStreamReader(inputStream))
            html = reader.use { it.readText() }
        }finally {
            inputStream?.close()
        }
        dataSoure = timeTableDeserialization(html!!, Group(groupName = "ИТ1901", isActive = true, dateOfFirstWeek = Date()))
    }

    override suspend fun getTimeTable(group: Group, typeSchedule: Int): Group {
        TODO("Not yet implemented")
    }

    override suspend fun getTimeTableActiveGroup(): List<TimeTableWithLesson> {
        return dataSoure
    }

    override suspend fun getTimeTableGroup(group: Group): List<TimeTableWithLesson> {
        return dataSoure
    }

    override suspend fun getDayTimeTable(
        groupName: String,
        isFirstWeek: Boolean,
        day: String
    ): List<TimeTableWithLesson> {
        return dataSoure.filter {
            ConverterUtils.formatterDay.format(it.timeTable.time) == day
            && isFirstWeek == it.timeTable.isFirstWeek
        }
    }

}