package com.doggystyle.table.usecases

import android.os.Build
import com.doggystyle.table.annotations.DayWeek
import com.doggystyle.table.model.pojo.DayTimeTable
import com.doggystyle.table.model.pojo.WeekTimeTable
import com.doggystyle.table.model.db.Group
import com.doggystyle.table.model.pojo.TimeTableWithLesson
import com.doggystyle.table.repositories.ITimeTableRepository
import com.doggystyle.table.utils.ConverterUtils
import java.time.Duration
import java.util.*
import javax.inject.Inject

class GetTimeTable @Inject constructor(private val repository: ITimeTableRepository,
                                       @DayWeek private val dayWeek: List<Pair<String, String>>,
                                       private val currentDate: Date): IGetTimeTable,
    UseCase<Pair<WeekTimeTable, WeekTimeTable>, Group>() {

    override suspend fun run(p: Group): Pair<WeekTimeTable, WeekTimeTable> {
        return repository.getTimeTableGroup(p).splitWeek()
    }

    override suspend fun getTimeTableGroup(group: Group): Pair<WeekTimeTable, WeekTimeTable> {
        return run(group)
    }

    private fun List<TimeTableWithLesson>.splitWeek(): Pair<WeekTimeTable, WeekTimeTable>{
        val currentWeek = getCurrentDayIndex(this[0].lesson.group?.dateOfFirstWeek?: Date())
        return this.filter { it.timeTable.isFirstWeek }.toList().splitDays(currentWeek) to
        this.filter { !it.timeTable.isFirstWeek }.toList().splitDays(!currentWeek)
    }

    private fun List<TimeTableWithLesson>.splitDays(currentWeek: Boolean): WeekTimeTable {
        var remainder = this
        val result = mutableListOf<DayTimeTable>()
        var middleList: Pair<List<TimeTableWithLesson>, List<TimeTableWithLesson>>
        while (remainder.isNotEmpty()){
            middleList = remainder.partition { it.timeTable.time.day == remainder[0].timeTable.time.day }
            result.add(
                DayTimeTable(
                    when{
                        currentWeek && currentDate.day == remainder[0].timeTable.time.day -> "Сегодня"
                        currentWeek && currentDate.day != 0 && currentDate.day + 1 == remainder[0].timeTable.time.day -> "Завтра"
                        !currentWeek && currentDate.day == 0 && remainder[0].timeTable.time.day == 1 -> "Завтра"
                        else -> dayWeek[remainder[0].timeTable.time.day].first
                    },
                    middleList.first
                )
            )
            remainder = middleList.second
        }
        return WeekTimeTable(result, currentWeek)
    }

    fun getCurrentDayIndex(date: Date): Boolean{
        val cal = Calendar.getInstance()
        cal.time = ConverterUtils.getFirstDayOfWeek(date)
        val cal2 = Calendar.getInstance()
        cal2.time = ConverterUtils.getFirstDayOfWeek(currentDate)
        val daysBetween =
            Duration.between(cal.toInstant(), cal2.toInstant()).toDays() + 1
        return ((daysBetween/7)%2 == 0L)
    }


}
