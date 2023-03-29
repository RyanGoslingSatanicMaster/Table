package com.doggystyle.table.repositories

import com.doggystyle.table.model.LoadingState
import com.doggystyle.table.model.db.Group
import com.doggystyle.table.model.pojo.TimeTableWithLesson
import com.doggystyle.table.model.requests.NextLessonRequest
import com.doggystyle.table.model.requests.TimeTableRequest
import com.doggystyle.table.services.ITimeTableService
import com.doggystyle.table.utils.Constant
import java.lang.Exception
import java.util.*
import javax.inject.Inject

class TimeTableRepository @Inject constructor(private val service: ITimeTableService): ITimeTableRepository {

    override suspend fun getTimeTable(group: Group, typeSchedule: Int): Group{
            return service.getTimeTable(group, typeSchedule)
    }

    override suspend fun getTimeTableActiveGroup(): List<TimeTableWithLesson> {
        return service.getTimeTableActiveGroup()
    }

    override suspend fun getTimeTableGroup(group: Group): List<TimeTableWithLesson> {
        return service.getTimeTableGroup(group)
    }

    override suspend fun getDayTimeTable(groupName: String, isFirstWeek: Boolean, day: String): List<TimeTableWithLesson> {
        return service.getDayTimeTable(groupName, isFirstWeek, day)
    }
}
