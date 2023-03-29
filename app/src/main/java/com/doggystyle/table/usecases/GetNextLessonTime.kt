package com.doggystyle.table.usecases

import com.doggystyle.table.annotations.DayWeek
import com.doggystyle.table.exceptions.TimeTableIsEmptyException
import com.doggystyle.table.model.pojo.TimeTableWithLesson
import com.doggystyle.table.model.requests.NextLessonRequest
import com.doggystyle.table.repositories.ITimeTableRepository
import com.doggystyle.table.utils.ConverterUtils
import java.time.Duration
import java.util.*
import javax.inject.Inject

class GetNextLessonTime @Inject constructor(
    private val repository: ITimeTableRepository,
    @DayWeek private val dayWeek: List<Pair<String, String>>
) : IGetNextLessonTime, UseCase<TimeTableWithLesson, NextLessonRequest>() {

    override suspend fun run(p: NextLessonRequest): TimeTableWithLesson {
        var isFirstWeek = ConverterUtils.isFirstWeek(p.group.dateOfFirstWeek!!, p.date)
        var compareDate =
            ConverterUtils.parseDateWithPrefix(ConverterUtils.formatter.format(p.date))
        var day = p.date.day
        do {
            val list = repository.getDayTimeTable(groupName = p.group.groupName,
                isFirstWeek = isFirstWeek,
                day = dayWeek[day].second)
            // TODO maybe check empty through exceptions
            if (list.isNotEmpty()) {
                val filtredList = list.filter {
                    (p.notify.first && p.notify.second
                            || p.notify.first && it.lesson.lesson.isLection
                            || p.notify.second && !it.lesson.lesson.isLection)
                            && compareDate.before(it.timeTable.time)
                }.sortedBy { it.timeTable.time }
                if (filtredList.isNotEmpty())
                    return filtredList[0].wrapToCurrentDate(p.date)
            }
            day = when (day) {
                6 -> 0
                else -> day + 1
            }
            if (day == 1) {
                isFirstWeek = !isFirstWeek
                compareDate = ConverterUtils.parseDateWithPrefix("Понедельник, 00:00")
            }
        } while (day != p.date.day)
        throw TimeTableIsEmptyException("TimeTable is Empty")
    }

    fun TimeTableWithLesson.wrapToCurrentDate(date: Date): TimeTableWithLesson {
        val calLesson = Calendar.getInstance().apply {
            time = this@wrapToCurrentDate.timeTable.time
        }
        val calendarDate = Calendar.getInstance().apply {
            time = date
        }
        calendarDate.apply {
            if (get(Calendar.DAY_OF_WEEK) != calLesson.get(Calendar.DAY_OF_WEEK)) {
                add(Calendar.DAY_OF_MONTH,
                    (calLesson.get(Calendar.DAY_OF_WEEK) + 7 - get(Calendar.DAY_OF_WEEK)) % 7);
            }
            set(Calendar.HOUR_OF_DAY, calLesson.get(Calendar.HOUR_OF_DAY))
            set(Calendar.MINUTE, calLesson.get(Calendar.MINUTE))
        }
        return this.copy(timeTable = this.timeTable.copy(time = calendarDate.time))
    }

    override suspend fun getNextLessonTime(request: NextLessonRequest): TimeTableWithLesson {
        return run(request)
    }

}
