package com.example.table.usecases

import android.os.Build
import android.util.Log
import com.example.table.model.pojo.DayTimeTable
import com.example.table.model.pojo.WeekTimeTable
import com.example.table.model.db.Group
import com.example.table.model.pojo.TimeTableWithLesson
import com.example.table.repositories.ITimeTableRepository
import com.google.gson.Gson
import java.time.Duration
import java.util.*
import javax.inject.Inject

class GetTimeTableUseCase @Inject constructor(private val repository: ITimeTableRepository,
                                              private val dayWeek: List<String>,
                                              private val currentDate: Date): IGetTimeTableUseCase,
    UseCase<Pair<WeekTimeTable, WeekTimeTable>, Group>() {

    override suspend fun run(p: Group): Pair<WeekTimeTable, WeekTimeTable> {
        return repository.getTimeTableGroup(p).splitWeek()
    }

    override suspend fun getTimeTableGroup(group: Group): Pair<WeekTimeTable, WeekTimeTable> {
        return run(group)
    }

    private fun List<TimeTableWithLesson>.splitWeek(): Pair<WeekTimeTable, WeekTimeTable>{
        val dateIndex = getCurrentDayIndex(this[0].lesson.group?.dateOfFirstWeek?: Date())
        return this.filter { it.timeTable.isFirstWeek }.toList().splitDays(dateIndex) to
        this.filter { !it.timeTable.isFirstWeek }.toList().splitDays(!dateIndex)
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
                        currentWeek && currentDate.day + 1 == remainder[0].timeTable.time.day -> "Завтра"
                        else -> dayWeek[remainder[0].timeTable.time.day]
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