package com.doggystyle.table.usecases

import com.doggystyle.table.model.db.Group
import com.doggystyle.table.model.requests.NextLessonRequest
import com.doggystyle.table.repository.TimeTableTestRepository
import com.doggystyle.table.utils.ConverterUtils
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
            calendar.setDate(2022, Calendar.NOVEMBER, 7).setTimeOfDay(9, 40, 0).build().time,
            calendar.setDate(2022, Calendar.NOVEMBER, 7).setTimeOfDay(11, 10, 0).build().time,
            calendar.setDate(2022, Calendar.NOVEMBER, 7).setTimeOfDay(13, 0, 0).build().time,
            calendar.setDate(2022, Calendar.NOVEMBER, 7).setTimeOfDay(14, 45, 0).build().time,
            calendar.setDate(2022, Calendar.NOVEMBER, 8).setTimeOfDay(11, 10, 0).build().time,
            calendar.setDate(2022, Calendar.NOVEMBER, 8).setTimeOfDay(13, 0, 0).build().time,
            calendar.setDate(2022, Calendar.NOVEMBER, 8).setTimeOfDay(15, 10, 0).build().time,
            calendar.setDate(2022, Calendar.NOVEMBER, 8).setTimeOfDay(16, 59, 0).build().time,
            calendar.setDate(2022, Calendar.NOVEMBER, 9).setTimeOfDay(7, 10, 0).build().time,
            calendar.setDate(2022, Calendar.NOVEMBER, 9).setTimeOfDay(9, 10, 0).build().time,
            calendar.setDate(2022, Calendar.NOVEMBER, 9).setTimeOfDay(11, 20, 0).build().time,
            calendar.setDate(2022, Calendar.NOVEMBER, 9).setTimeOfDay(13, 40, 0).build().time,
            calendar.setDate(2022, Calendar.NOVEMBER, 10).setTimeOfDay(6, 10, 0).build().time,
            calendar.setDate(2022, Calendar.NOVEMBER, 10).setTimeOfDay(9, 30, 0).build().time,
            calendar.setDate(2022, Calendar.NOVEMBER, 10).setTimeOfDay(11, 15, 0).build().time,
            calendar.setDate(2022, Calendar.NOVEMBER, 10).setTimeOfDay(12, 40, 0).build().time,
            calendar.setDate(2022, Calendar.NOVEMBER, 11).setTimeOfDay(7, 55, 0).build().time,
            calendar.setDate(2022, Calendar.NOVEMBER, 11).setTimeOfDay(11, 15, 0).build().time,
            calendar.setDate(2022, Calendar.NOVEMBER, 11).setTimeOfDay(12, 40, 0).build().time,
            calendar.setDate(2022, Calendar.NOVEMBER, 12).build().time,
            calendar.setDate(2022, Calendar.NOVEMBER, 13).build().time
        )
    }

    @Test
    fun getNextDayTest() = runBlocking {
        val results = listOf("11:30", "11:30", "13:50", "11:30",
                        "11:30", "13:50", "15:35", "17:20",
                        "08:00", "09:45", "11:30", "08:00",
                        "08:00", "09:45", "11:30", "13:50",
                        "09:45", "11:30", "09:45", "09:45", "09:45" ) to
                    listOf("09:45", "11:30", "13:50", "11:30",
                        "11:30", "13:50", "15:35", "08:00",
                        "08:00", "09:45", "11:30", "13:50",
                        "09:45", "09:45", "11:30", "11:30",
                        "11:30", "11:30", "13:50", "11:30", "11:30" )

        listOfDayWeek.forEachIndexed { index, el ->
            getNextLessonTime = GetNextLessonTime(
                TimeTableTestRepository(),
                stringDayOfWeek
            )
            val date = getNextLessonTime.getNextLessonTime(
                NextLessonRequest(
                    Triple(true, true, 5),
                    Group(0, "ИТ1901", true, el),
                )
            )
            if (results.first[index] != ConverterUtils.formatterTime.format(date.timeTable.time))
                println(results.first[index] + "     " + date.timeTable + "      " + el)
            assert(results.first[index] == ConverterUtils.formatterTime.format(date.timeTable.time))
        }
    }

}
