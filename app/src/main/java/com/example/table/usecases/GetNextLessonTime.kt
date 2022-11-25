package com.example.table.usecases

import android.os.Build
import com.example.table.annotations.DayWeek
import com.example.table.exceptions.TimeTableIsEmptyException
import com.example.table.model.requests.NextLessonRequest
import com.example.table.repositories.ITimeTableRepository
import com.example.table.utils.ConverterUtils
import java.time.Duration
import java.util.*
import javax.inject.Inject

class GetNextLessonTime @Inject constructor(private val repository: ITimeTableRepository,
                                            @DayWeek private val dayWeek: List<Pair<String, String>>,
                                            private val currentDate: Date
): IGetNextLessonTime, UseCase<Date, NextLessonRequest>() {

    override suspend fun run(p: NextLessonRequest): Date {
        var isFirstWeek = ConverterUtils.isFirstWeek(p.group.dateOfFirstWeek!!, currentDate)
        var day = when(currentDate.day){
            6 -> 0
            else -> currentDate.day + 1
        }
        while (day != currentDate.day){
            if (day == 1)
                isFirstWeek = !isFirstWeek
            val list = repository.getDayTimeTable(groupName = p.group.groupName, isFirstWeek = isFirstWeek, day = dayWeek[day].second)
            // TODO maybe check empty through exceptions
            if (list.isNotEmpty()) {
                val filtredList = list.filter {
                    p.notify.first && p.notify.second
                    || p.notify.first && it.lesson.lesson.isLection
                    || p.notify.second && !it.lesson.lesson.isLection
                }.sortedBy { it.timeTable.time }
                if (filtredList.isNotEmpty())
                    return filtredList.get(0).timeTable.time
            }
            day = when(day){
                6 -> 0
                else -> day + 1
            }
        }
        throw TimeTableIsEmptyException("TimeTable is Empty")
    }

    override suspend fun getNextLessonTime(request: NextLessonRequest): Date {
        return run(request)
    }

}