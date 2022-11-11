package com.example.table.usecases

import android.os.Build
import com.example.table.model.pojo.DayTimeTable
import com.example.table.model.pojo.TimeTableWithLesson
import com.example.table.repositories.ITimeTableRepository
import java.time.Duration
import java.util.*
import javax.inject.Inject

class GetDayWidget @Inject constructor(private val timeTableRepository: ITimeTableRepository,
                                       private val dayWeek: List<String>,
                                       private val currentDate: Date
): UseCase<DayTimeTable, Int>(), IGetDayWidget {

    override suspend fun run(p: Int): DayTimeTable? {
        try {
            return timeTableRepository.getTimeTableActiveGroup().splitDay(if (p == -1) currentDate.day else p)
        }catch (ex: Exception){
            return null
        }
    }

    override suspend fun getActiveDayWidget(index: Int): DayTimeTable? {
        return run(index)
    }

    private fun List<TimeTableWithLesson>.splitDay(index: Int): DayTimeTable?{
        val isFirstWeek = getCurrentWeekIndex(this[0].lesson.group?.dateOfFirstWeek?: Date())
        return DayTimeTable(
            when{
                index == currentDate.day -> "Сегодня"
                index == currentDate.day + 1 -> "Завтра"
                else -> dayWeek[index]
            },
            this.filter { it.timeTable.isFirstWeek == isFirstWeek && it.timeTable.time.day == index }
        )
    }

    fun getCurrentWeekIndex(date: Date): Boolean{
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