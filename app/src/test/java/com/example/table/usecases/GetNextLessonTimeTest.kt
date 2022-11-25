package com.example.table.usecases

import com.example.table.model.db.Group
import com.example.table.model.requests.NextLessonRequest
import com.example.table.repository.TimeTableTestRepository
import com.example.table.utils.ConverterUtils
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.*

class GetNextLessonTimeTest{

    private lateinit var getNextLessonTime: IGetNextLessonTime

    private lateinit var listOfDayWeek: List<Date>

    private val stringDayOfWeek = listOf(
        "Воскресение" to "вск",
        "Понедельник" to "пн",
        "Вторник" to "вт",
        "Среда" to "ср",
        "Четверг" to "чт",
        "Пятница" to "пт",
        "Суббота" to "сб")

    @Before
    fun setUp(){
        val calendar = Calendar.Builder()
        listOfDayWeek = listOf(
            calendar.setDate(2022, Calendar.NOVEMBER, 7).build().time,
            calendar.setDate(2022, Calendar.NOVEMBER, 8).build().time,
            calendar.setDate(2022, Calendar.NOVEMBER, 9).build().time,
            calendar.setDate(2022, Calendar.NOVEMBER, 10).build().time,
            calendar.setDate(2022, Calendar.NOVEMBER, 11).build().time,
            calendar.setDate(2022, Calendar.NOVEMBER, 12).build().time,
            calendar.setDate(2022, Calendar.NOVEMBER, 13).build().time
        )
    }

    @Test
    fun getNextDayTest() = runBlocking {
        val results = listOf("11:30", "08:00", "08:00", "09:45", "09:45", "09:45", "09:45")
        listOfDayWeek.forEachIndexed { index, el ->
            getNextLessonTime = GetNextLessonTime(
                TimeTableTestRepository(),
                stringDayOfWeek,
                el
            )
            val date = getNextLessonTime.getNextLessonTime(
                NextLessonRequest(
                true to true,
                    Group(0, "ИТ1901", true, el)
                )
            )
            assert(results[index] == ConverterUtils.formatterTime.format(date))
        }
    }

}