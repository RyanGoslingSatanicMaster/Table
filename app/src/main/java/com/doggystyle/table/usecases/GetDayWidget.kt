package com.doggystyle.table.usecases

import android.os.Build
import com.doggystyle.table.annotations.DayWeek
import com.doggystyle.table.model.pojo.DayTimeTable
import com.doggystyle.table.model.pojo.TimeTableWithLesson
import com.doggystyle.table.repositories.ITimeTableRepository
import com.doggystyle.table.utils.ConverterUtils
import java.time.Duration
import java.util.*
import javax.inject.Inject

class GetDayWidget @Inject constructor(private val timeTableRepository: ITimeTableRepository,
                                       @DayWeek private val dayWeek: List<Pair<String, String>>,
                                       private val currentDate: Date
): UseCase<DayTimeTable, Int>(), IGetDayWidget {

    override suspend fun run(p: Int): DayTimeTable? {
        try {
            return timeTableRepository.getTimeTableActiveGroup().splitDay(p)
        }catch (ex: Exception){
            return null
        }
    }

    override suspend fun getActiveDayWidget(index: Int): DayTimeTable? {
        return run(index)
    }

    private fun List<TimeTableWithLesson>.splitDay(index: Int): DayTimeTable?{
        var isFirstWeek = ConverterUtils.isFirstWeek(this[0].lesson.group?.dateOfFirstWeek!!, currentDate)
        val dayIndex = when {
            currentDate.day == 0 ->{
                isFirstWeek = !isFirstWeek
                if (index == -1)
                    1
                else
                    index
            }
            index == -1 && currentDate.day != 0 -> currentDate.day
            else -> index
        }
        return DayTimeTable(
            when{
                dayIndex == currentDate.day -> "Сегодня"
                dayIndex == currentDate.day + 1 -> "Завтра"
                else -> dayWeek[dayIndex].first
            },
            this.filter { it.timeTable.isFirstWeek == isFirstWeek && it.timeTable.time.day == dayIndex }
        )
    }

}
