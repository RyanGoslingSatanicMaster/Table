package com.doggystyle.table.services

import com.doggystyle.table.model.db.Group
import com.doggystyle.table.model.pojo.TimeTableWithLesson
import com.doggystyle.table.model.requests.NextLessonRequest
import com.doggystyle.table.model.requests.TimeTableRequest
import java.util.*

interface ITimeTableService {
    suspend fun getTimeTable(group: Group, typeSchedule: Int): Group
    suspend fun getDayTimeTable(groupName: String, isFirstWeek: Boolean, day: String): List<TimeTableWithLesson>
    suspend fun getTimeTableActiveGroup(): List<TimeTableWithLesson>
    suspend fun getTimeTableGroup(group: Group): List<TimeTableWithLesson>
}
